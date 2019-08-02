/*
 * Copyright 2019-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.tremblay.roi.service;

import org.apache.commons.lang3.tuple.Pair;
import pro.tremblay.roi.domain.Account;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Position;
import pro.tremblay.roi.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserDataService extends DependencyService {

    private final List<Account> accounts = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public UserDataService() {
    }

    public UserDataService(boolean isThrottling) {
        super(isThrottling);
    }

    //
    // Methods to call during test to record a fake behavior
    //

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    ///////////////////////

    /**
     * Returns the currency the user wants the report in.
     *
     * @return user's currency
     */
    public Currency getUserCurrency() {
        return Currency.CAD;
    }

    /**
     * Get account states as of today and transaction since the beginning of the period.
     *
     * @param firstDayOfPeriod first day from which we get transactions
     * @return user accounts and transactions since the beginning of the period
     */
    public Pair<List<Account>, List<Transaction>> getUserData(LocalDate firstDayOfPeriod) {
        throttle(50);
        return Pair.of(accounts, transactions);
    }

    /**
     * Get prices of all securities on all positions on all accounts.
     *
     * @param accounts the accounts containing the securities to get prices for
     * @return a pair of positions (left being priceable ones, right being none priceable ones)
     */
    public Pair<List<Position>, List<Position>> getPriceablePositions(Collection<Account> accounts) {
        List<Position> priceablePositions = new ArrayList<>();
        List<Position> otherPositions = new ArrayList<>();
        for (Account account : accounts) {
            for (Position position : account.getPositions()) {
                if (position.getSecurity() == null) {
                    log.warn("No security on position");
                    otherPositions.add(position);
                    continue;
                }
                priceablePositions.add(position);
            }
        }
        return Pair.of(priceablePositions, otherPositions);
    }

    /**
     * Sums cash from all accounts by currencies.
     *
     * @param accounts accounts to sum up
     * @return a map of the different currencies and the sum of cash for each of them
     */
    public Map<Currency, BigDecimal> calculateCashPosition(Collection<Account> accounts) {
        return accounts.stream().collect(Collectors.toMap(Account::getCurrency, Account::getCash,
                BigDecimal::add));
    }

    /**
     * Creates a map of accounts per names.
     *
     * @param accounts list of accounts to map
     * @return map of accounts per names
     */
    public Map<String, Account> getAccountsPerName(List<Account> accounts) {
        return accounts.stream()
                .collect(Collectors.toMap(Account::getName, Function.identity()));
    }
}
