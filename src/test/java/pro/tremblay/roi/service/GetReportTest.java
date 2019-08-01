package pro.tremblay.roi.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import pro.tremblay.roi.domain.Account;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Position;
import pro.tremblay.roi.domain.Security;
import pro.tremblay.roi.domain.Transaction;
import pro.tremblay.roi.domain.TransactionType;
import pro.tremblay.roi.service.dto.PriceDTO;
import pro.tremblay.roi.service.dto.ReportingDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.tremblay.roi.util.BigDecimalUtil.bd;

public class GetReportTest extends ReportingServiceTest {


    @Test
    public void getReport_doNothing() {
        ReportingDTO result = reportingService.getReport(oneYearAgo);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(result.getInitial()).isEqualTo("0.00");
        soft.assertThat(result.getCurrent()).isEqualTo("0.00");
        soft.assertThat(result.getFee()).isEqualTo("0.00");
        soft.assertThat(result.getNetDeposits()).isEqualTo("0.00");
        soft.assertThat(result.getNetVariation().getAmount()).isEqualTo("0.00");
        soft.assertThat(result.getNetVariation().getPercentage()).isEqualTo("0.0000");

        List<PriceDTO> valuePerDay = result.getValuePerDay();
        soft.assertThat(valuePerDay).hasSize(366);

        soft.assertThat(result.getAnomalies()).isEmpty();

        soft.assertAll();

        assertPrice(valuePerDay.get(0), oneYearAgo, bd(0));
        assertPrice(valuePerDay.get(365), now, bd(0));
    }

    @Test
    public void getReport_doNotModifyOriginalEntities() {
        Security security = security();

        priceService.addPrice(security, bd(2L));

        Account account = new Account()
                .name(ACCOUNT_NAME)
                .cash(BigDecimal.TEN)
                .currency(Currency.CAD);
        userDataService.addAccount(account);

        Position position = new Position()
                .security(security)
                .quantity(100L);
        account.addPosition(position);

        Transaction transaction = new Transaction()
                .accountName(ACCOUNT_NAME)
                .type(TransactionType.sell)
                .amount(BigDecimal.ONE)
                .currency(Currency.CAD)
                .fee(bd(2))
                .quantity(-10L)
                .tradeDate(sixMonthsAgo)
                .security(security);
        userDataService.addTransaction(transaction);

        reportingService.getReport(oneYearAgo);

        // The original entities are not supposed to be modified by the reporting process
        assertThat(account.getName()).isEqualTo(ACCOUNT_NAME);
        assertThat(account.getCash()).isEqualTo(BigDecimal.TEN);
        assertThat(account.getCurrency()).isEqualTo(Currency.CAD);
        assertThat(account.getPositions()).containsOnly(position);

        assertThat(security.getSymbol()).isEqualTo(SYMBOL);
        assertThat(security.getCurrency()).isEqualTo(Currency.CAD);

        assertThat(transaction.getAccountName()).isEqualTo(ACCOUNT_NAME);
        assertThat(transaction.getType()).isEqualTo(TransactionType.sell);
        assertThat(transaction.getSecurity()).isEqualTo(security);
        assertThat(transaction.getCurrency()).isEqualTo(Currency.CAD);
        assertThat(transaction.getFee()).isEqualTo(bd(2));
        assertThat(transaction.getQuantity()).isEqualTo(-10L);
        assertThat(transaction.getTradeDate()).isEqualTo(sixMonthsAgo);
        assertThat(transaction.getSecurity()).isEqualTo(security);

        assertThat(position.getSecurity()).isEqualTo(security);
        assertThat(position.getQuantity()).isEqualTo(100L);
    }

    @Test
    public void getReport_moneyAtBeginningStillAtTheEnd() {
        Account account = new Account()
                .name(ACCOUNT_NAME)
                .cash(BigDecimal.TEN)
                .currency(Currency.CAD);
        userDataService.addAccount(account);

        ReportingDTO result = reportingService.getReport(oneYearAgo);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(result.getInitial()).isEqualTo("10.00");
        soft.assertThat(result.getCurrent()).isEqualTo("10.00");
        soft.assertThat(result.getNetDeposits()).isEqualTo("0.00");
        soft.assertThat(result.getFee()).isEqualTo("0.00");
        soft.assertThat(result.getNetVariation().getAmount()).isEqualTo("0.00");
        soft.assertThat(result.getNetVariation().getPercentage()).isEqualTo("0.0000");

        List<PriceDTO> valuePerDay = result.getValuePerDay();
        soft.assertThat(valuePerDay).hasSize(366);

        soft.assertThat(result.getAnomalies()).isEmpty();

        soft.assertAll();

        assertPrice(valuePerDay.get(0), oneYearAgo, bd(10));
        assertPrice(valuePerDay.get(365), now, bd(10));
    }

    @Test
    public void getReport_addCash() {
        Account account = new Account()
                .name(ACCOUNT_NAME)
                .cash(BigDecimal.TEN)
                .currency(Currency.CAD);
        userDataService.addAccount(account);

        Transaction transaction = new Transaction()
                .accountName(ACCOUNT_NAME)
                .type(TransactionType.deposit)
                .amount(BigDecimal.ONE)
                .currency(Currency.CAD)
                .fee(BigDecimal.ZERO)
                .quantity(0L)
                .tradeDate(sixMonthsAgo)
                .security(null);
        userDataService.addTransaction(transaction);

        ReportingDTO result = reportingService.getReport(oneYearAgo);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(result.getInitial()).isEqualTo("9.00");
        soft.assertThat(result.getCurrent()).isEqualTo("10.00");
        soft.assertThat(result.getNetDeposits()).isEqualTo("1.00");
        soft.assertThat(result.getFee()).isEqualTo("0.00");
        soft.assertThat(result.getNetVariation().getAmount()).isEqualTo("1.00");
        soft.assertThat(result.getNetVariation().getPercentage()).isEqualTo("0.1111");

        List<PriceDTO> valuePerDay = result.getValuePerDay();
        soft.assertThat(valuePerDay).hasSize(366);

        soft.assertThat(result.getAnomalies()).isEmpty();

        soft.assertAll();

        assertPrice(valuePerDay.get(0), oneYearAgo, bd(9));
        assertPrice(valuePerDay.get(184), sixMonthsAgo, bd(9));
        assertPrice(valuePerDay.get(185), sixMonthsAgo.plusDays(1), bd(10));
        assertPrice(valuePerDay.get(365), now, bd(10));
    }

    @Test
    public void getReport_addPosition() {
        Security security = security();

        priceService.addPrice(security, bd(2L));

        Position position = new Position()
                .quantity(100L)
                .security(security);

        Account account = new Account()
                .name(ACCOUNT_NAME)
                .cash(BigDecimal.TEN)
                .currency(Currency.CAD)
                .addPosition(position);

        userDataService.addAccount(account);

        Transaction transaction = new Transaction()
                .accountName(ACCOUNT_NAME)
                .type(TransactionType.buy)
                .amount(bd(-5L))
                .currency(Currency.CAD)
                .fee(BigDecimal.ZERO)
                .quantity(100L)
                .tradeDate(sixMonthsAgo)
                .security(security);
        userDataService.addTransaction(transaction);

        ReportingDTO result = reportingService.getReport(oneYearAgo);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(result.getInitial()).isEqualTo("15.00");
        soft.assertThat(result.getCurrent()).isEqualTo("210.00");
        soft.assertThat(result.getNetDeposits()).isEqualTo("0.00");
        soft.assertThat(result.getFee()).isEqualTo("0.00");
        soft.assertThat(result.getNetVariation().getAmount()).isEqualTo("195.00");
        soft.assertThat(result.getNetVariation().getPercentage()).isEqualTo("13.0000");

        List<PriceDTO> valuePerDay = result.getValuePerDay();
        soft.assertThat(valuePerDay).hasSize(366);

        soft.assertThat(result.getAnomalies()).isEmpty();

        soft.assertAll();

        assertPrice(valuePerDay.get(0), oneYearAgo, bd(15));
        assertPrice(valuePerDay.get(184), sixMonthsAgo, bd(15));
        assertPrice(valuePerDay.get(185), sixMonthsAgo.plusDays(1), bd(210));
        assertPrice(valuePerDay.get(365), now, bd(210));
    }

    @Test
    public void getReport_transactionWithFees() {
        Security security = security();

        priceService.addPrice(security, bd(2L));

        Account account = new Account()
                .name(ACCOUNT_NAME)
                .cash(bd(1000))
                .currency(Currency.CAD);

        userDataService.addAccount(account);

        Transaction transaction1 = new Transaction()
                .accountName(ACCOUNT_NAME)
                .type(TransactionType.buy)
                .amount(bd(-5L))
                .currency(Currency.CAD)
                .fee(bd(5))
                .quantity(100L)
                .tradeDate(sixMonthsAgo)
                .security(security);
        userDataService.addTransaction(transaction1);

        Transaction transaction2 = new Transaction()
                .accountName(ACCOUNT_NAME)
                .type(TransactionType.buy)
                .amount(bd(-5L))
                .currency(Currency.CAD)
                .fee(bd(15))
                .quantity(100L)
                .tradeDate(sixMonthsAgo)
                .security(security);
        userDataService.addTransaction(transaction2);

        Transaction transaction3 = new Transaction()
                .accountName(ACCOUNT_NAME)
                .type(TransactionType.buy)
                .amount(bd(-5L))
                .currency(Currency.CAD)
                .fee(bd(20))
                .quantity(100L)
                .tradeDate(sixMonthsAgo)
                .security(security);
        userDataService.addTransaction(transaction3);

        ReportingDTO result = reportingService.getReport(oneYearAgo);

        assertThat(result.getFee()).isEqualTo("40.00");
    }

    private Security security() {
        return new Security()
                .symbol(SYMBOL)
                .currency(Currency.CAD);
    }

    private void assertPrice(PriceDTO price, LocalDate date, BigDecimal value) {
        assertThat(price.getDate()).isEqualTo(date);
        assertThat(price.getValue()).isEqualTo(value);
    }

    @SuppressWarnings("unused")
    private void printPrices(List<PriceDTO> prices) {
        for (int i = 0; i < prices.size(); i++) {
            System.out.println(i + ": " + prices.get(i));
        }
    }

}
