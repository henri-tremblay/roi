package pro.tremblay.roi.service;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.tremblay.roi.domain.Account;
import pro.tremblay.roi.domain.AnomalyType;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Position;
import pro.tremblay.roi.domain.Security;
import pro.tremblay.roi.domain.Transaction;
import pro.tremblay.roi.domain.TransactionType;
import pro.tremblay.roi.domain.Variation;
import pro.tremblay.roi.service.dto.PriceDTO;
import pro.tremblay.roi.service.dto.ReportingDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pro.tremblay.roi.util.BigDecimalUtil.*;

public class ReportingService {

    private final Logger log = LoggerFactory.getLogger(ReportingService.class);

    private final UserDataService userDataService;

    private final PriceService priceService;

    private final ExchangeRateService exchangeRateService;

    private final MessageService messageService;

    public ReportingService(UserDataService userDataService, PriceService priceService, ExchangeRateService exchangeRateService, MessageService messageService) {
        this.userDataService = userDataService;
        this.priceService = priceService;
        this.exchangeRateService = exchangeRateService;
        this.messageService = messageService;
    }

    /**
     * Calculate report from first day up to now.
     *
     * @param firstDayOfPeriod first day of the period
     * @return the report
     */
    public ReportingDTO getReport(LocalDate firstDayOfPeriod) {
        LocalDate lastDay = LocalDate.now();

        Pair<List<Account>, List<Transaction>> data = userDataService.getUserData(firstDayOfPeriod);
        List<Account> accounts = data.getLeft();
        List<Transaction> transactions = data.getRight();

        Pair<List<Position>, List<Position>> positions = userDataService.getPriceablePositions(accounts);
        List<Position> finalPositions = positions.getLeft();

        Pair<MultiKeyMap<Object, BigDecimal>, Collection<String>> priceQuotesWithErrors = priceService.getPricesForPositions(finalPositions, firstDayOfPeriod, lastDay);

        return getReportForPeriod(firstDayOfPeriod, lastDay, accounts, transactions, priceQuotesWithErrors);
    }

    /**
     * Calculate a report from the given parameters. This method will not be called directly by production code but
     * is useful during testing.
     *
     * @param firstDayOfPeriod first day of the period (inclusive)
     * @param lastDay last day of the period (inclusive)
     * @param accounts all accounts to be processed
     * @param transactions all transaction to be processed
     * @param priceQuotesWithErrors prices for all positions we have
     * @return the report
     */
    ReportingDTO getReportForPeriod(LocalDate firstDayOfPeriod, LocalDate lastDay, List<Account> accounts, List<Transaction> transactions, Pair<MultiKeyMap<Object, BigDecimal>, Collection<String>> priceQuotesWithErrors) {
        ReportingDTO report = new ReportingDTO();

        BigDecimal commissions = BigDecimal.ZERO;
        List<PriceDTO> dailyAmount = new ArrayList<>();

        // Message used in the method to file anomalies
        String message = messageService.getMessage("problem.notAvailable");

        // Clone the accounts to prevent modifying the original ones when reverting transactions
        Map<String, Account> accountsPerName = SerializationUtils.clone((Serializable & Map<String, Account>)  userDataService.getAccountsPerName(accounts));

        Pair<List<Position>, List<Position>> positions = userDataService.getPriceablePositions(accountsPerName.values());
        List<Position> otherPositions = positions.getRight();
        // Check for Missing symbols and save in a list for reporting
        MultiKeyMap<Object, BigDecimal> datesQuotesMap = priceQuotesWithErrors.getLeft();
        Collection<String> listOfMissingQuotes = priceQuotesWithErrors.getRight();

        BigDecimal finalPortfolioValue = calculatePortfolioValue(lastDay, accountsPerName.values(), datesQuotesMap);

        //  To compute NetDeposits we need the transactions during the period including first and last day
        List<Transaction> transactionsInRange = transactions.stream()
                .filter(t -> t.getTradeDate().isAfter(firstDayOfPeriod.minusDays(1)) && t.getTradeDate().isBefore(lastDay.plusDays(1)))
                .collect(Collectors.toList());

        BigDecimal netDeposits = calculateNetDeposits(transactionsInRange);

        report.setNetDeposits(netDeposits.setScale(2, RoundingMode.HALF_UP));

        dailyAmount.add(PriceDTO.of(lastDay, finalPortfolioValue.setScale(1, RoundingMode.HALF_UP)));

        List<Position> negativePositions = Collections.emptyList();
        for (LocalDate computationDay = lastDay; computationDay.isAfter(firstDayOfPeriod); ) {
            // Loop to the day before. today is also moving backward in time
            computationDay = computationDay.minusDays(1);

            // Cannot use date variable in stream comparison
            LocalDate finalComputationDay = computationDay;
            List<Transaction> transactionsOfTheDay = transactions.stream()
                    .filter(t -> t.getTradeDate().isEqual(finalComputationDay))
                    .collect(Collectors.toList());

            // The last two fields are not used by this task as far as I can tell
            revertTransactions(accountsPerName, transactionsOfTheDay);

            //  Check if we have a new security popping up from the past with no quantity at end of period
            List<Position> allPositions = userDataService.getPriceablePositions(accountsPerName.values()).getLeft();

            // For the day
            commissions = commissions.add(calculateCommission(transactionsOfTheDay));
            calculateDailyPosition(computationDay, accountsPerName, datesQuotesMap, dailyAmount);

            //  Check for Negative Position to set anomaly later, for now no anomaly if negative after initial date of period
            negativePositions = allPositions.stream()
                    .filter( position -> position.getQuantity() < 0 && position.getSecurity().getSymbol() != null)
                    .collect(Collectors.toList());
        }

        // Reverse to have the oldest date first
        Collections.reverse(dailyAmount);

        //  Calculate gain from non-priceable portion of portfolio and scale over a period of one year, actual period is given by size()
        //  We keep it at zero for now
        BigDecimal portfolioGain = BigDecimal.ZERO;

        report.setCurrent(finalPortfolioValue.setScale(2, RoundingMode.HALF_UP));

        BigDecimal initialAmount = dailyAmount.get(0).getValue().subtract(portfolioGain);
        report.setInitial(initialAmount.setScale(2, RoundingMode.HALF_UP));

        Variation totalReturn = Variation.variationFromTotal(report.getInitial(), report.getCurrent().subtract(report.getInitial()));
        totalReturn = totalReturn.scale(2, 4);

        report.setNetVariation(totalReturn);

        report.setFee(commissions.setScale(2, RoundingMode.HALF_UP));

        // Add portfolio gain to dailyValue linearly over the whole period, period is given by size()
        List<PriceDTO> adjustedDailyAmount = dailyAmount.stream()
                .map(v -> PriceDTO.of(v.getDate(),v.getValue()
                        .add(BigDecimal.valueOf(portfolioGain.doubleValue()))
                        .setScale(0,RoundingMode.HALF_UP)))
                .collect(Collectors.toList());

        report.setValuePerDay(adjustedDailyAmount);
        listOfMissingQuotes.forEach(item -> {
            if (item.startsWith("null")) {
                item = item.substring(6);
                report.addAnomaly(AnomalyType.NO_PRICE_AVAILABLE, message + " : " + item);
            }
            else {
                report.addAnomaly(AnomalyType.NO_PRICE_AVAILABLE, item);
            }

        });
        otherPositions.forEach(item -> {
            if (item.getSecurity() != null) {
                if (item.getSecurity().getSymbol() == null) {
                    report.addAnomaly(AnomalyType.NO_PRICE_AVAILABLE, "no symbol");
                }
            }
        });

        negativePositions.forEach(item -> report.addAnomaly(AnomalyType.NEGATIVE_INITIAL_VALUE, item.getSecurity().getSymbol()));

        return report;
    }

    /**
     * For all transactions, calculate the net deposit (e.g. deposit - withdrawal) in the user currency. Transactions
     * that are not deposits or withdrawals are ignored.
     *
     * @param transactions all transactions to process
     * @return the net deposit
     */
    BigDecimal calculateNetDeposits(List<Transaction> transactions) {
        return sum(transactions.stream()
                .filter(t -> t.getAmount() != null)
                .peek(o -> System.out.println("a"))
                .filter(t -> t.getType().equals(TransactionType.deposit) || t.getType().equals(TransactionType.withdrawal))
                .peek(o -> System.out.println("b"))
                .map(t -> t.getAmount().multiply(getExchangeRateAtTradeDate(t))));
    }

    /**
     * Get EOD currency exchange rate at a given date between the transaction currency and the user currency.
     *
     * @param transaction transaction for which we want to rate
     * @return rate, in user currency
     */
    BigDecimal getExchangeRateAtTradeDate(Transaction transaction) {
        Currency currency = transaction.getCurrency();
        LocalDate tradeDate = transaction.getTradeDate();
        if(currency == null || tradeDate == null) {
            return BigDecimal.ZERO;
        }
        return exchangeRateService.getExchangeRate(currency, userDataService.getUserCurrency(), tradeDate);
    }

    /**
     * Calculate commissions on transaction. It is basically the sum of the fees on all transactions.
     *
     * @param transactions the transactions for which we want to calculate the commission
     * @return the commission, in user currency
     */
    BigDecimal calculateCommission(List<Transaction> transactions) {
        BigDecimal amount = BigDecimal.ZERO;
        for(Transaction transaction : transactions) {
            BigDecimal fee = transaction.getFee();
            if(fee == null) {
                continue;
            }
            Currency currency = transaction.getCurrency();
            if (currency == null) {
                continue;
            }

            BigDecimal rate = getExchangeRateAtTradeDate(transaction);
            amount = amount.add(rate.multiply(fee));
        }

        return amount;
    }

    /**
     * Calculate the daily position. This position is a {@link PriceDTO} added to {@code dailyAmount}.
     *
     * @param date date for which we want to calculate the position
     * @param accountsPerName map of accounts by the name
     * @param prices prices of security by date, symbol and currency
     * @param dailyAmount list off daily amounts where the newly calculated position will be added
     */
    private void calculateDailyPosition(LocalDate date, Map<String, Account> accountsPerName,
                                         MultiKeyMap<Object, BigDecimal> prices, List<PriceDTO> dailyAmount) {

        BigDecimal oldPortfolioValue = calculatePortfolioValue(date, accountsPerName.values(), prices);

        dailyAmount.add(PriceDTO.of(date, oldPortfolioValue));
    }

    /**
     * Revert all transactions. It means the impact of the transaction on the current position will be reverted
     * @param accountsPerName current position as a map of accounts by name
     * @param transactions transactions to revert
     */
    private void revertTransactions(Map<String, Account> accountsPerName, List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            log.debug("Reverting {}", transaction);
            transaction.getType().revertTransaction(transaction, accountsPerName);
        });
    }

    /**
     * Calculate the current value of a portfolio. Including security value and cash amounts.
     *
     * @param date date of the evaluation
     * @param accounts accounts to calculate
     * @param prices prices of security by date, symbol and currency
     * @return portfolio value at the given date
     */
    private BigDecimal calculatePortfolioValue(LocalDate date, Collection<Account> accounts, MultiKeyMap<Object, BigDecimal> prices) {
        log.debug("Calculate portfolio value at {}", date);

        List<Position> positions = userDataService.getPriceablePositions(accounts).getLeft();

        log.debug("Priceable positions: {}", positions);

        Map<Currency, BigDecimal> cashPositions = userDataService.calculateCashPosition(accounts);

        log.debug("Cash positions: {}", cashPositions);

        return calculateCashValue(date, cashPositions).add(calculateSecurityValue(date, positions, prices));
    }

    /**
     * Calculate cash value of cash positions.
     *
     * @param date date of the positions
     * @param cashPositions all cash positions by currency
     * @return the cash position in the user currency
     */
    BigDecimal calculateCashValue(LocalDate date, Map<Currency, BigDecimal> cashPositions) {
        return sum(cashPositions.entrySet().stream()
                .map(e -> {
                    Currency currency = e.getKey();
                    BigDecimal rate = exchangeRateService.getExchangeRate(currency, userDataService.getUserCurrency(), date);
                    return rate.multiply(e.getValue());
                }));
    }

    /**
     * Calculate security value of security positions.
     *
     * @param date date of the positions
     * @param positions security positions
     * @param prices prices of security by date, symbol and currency
     * @return security value in the user currency
     */
    BigDecimal calculateSecurityValue(LocalDate date, List<Position> positions, MultiKeyMap<Object, BigDecimal> prices) {
        return sum(positions.stream().map((Position pos) -> {
            Security security = pos.getSecurity();
            Currency currency = security.getCurrency();
            String symbol = security.getSymbol();

            BigDecimal price = prices.get(date, symbol, currency);
            if(price == null) {
                log.error("We should have a price");
                return BigDecimal.ZERO;
            }

            BigDecimal dayRate = exchangeRateService.getExchangeRate(currency, userDataService.getUserCurrency(), date);
            BigDecimal quantity = bd(pos.getQuantity());

            return price.multiply(quantity).multiply(dayRate);
        }));
    }

}
