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
package pro.tremblay.roi.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import pro.tremblay.roi.domain.Account;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Position;
import pro.tremblay.roi.domain.Security;
import pro.tremblay.roi.domain.Transaction;
import pro.tremblay.roi.domain.TransactionType;
import pro.tremblay.roi.service.ExchangeRateService;
import pro.tremblay.roi.service.MessageService;
import pro.tremblay.roi.service.PriceService;
import pro.tremblay.roi.service.ReportingService;
import pro.tremblay.roi.service.UserDataService;
import pro.tremblay.roi.service.dto.ReportingDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pro.tremblay.roi.util.BigDecimalUtil.bd;

@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 10, time = 2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class RoiBenchmark {

    private final LocalDate lastYear = LocalDate.now().minusYears(1);

    private final UserDataService userDataService = new UserDataService(true);
    private final PriceService priceService = new PriceService(true);
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(true);
    private final MessageService messageService = new MessageService(true);

    private final ReportingService service = new ReportingService(userDataService, priceService, exchangeRateService, messageService);

    @Setup
    public void setUp() {
        Random random = new Random();
        // Let's assume 50 different securities
        List<Security> securities = IntStream.range(0, 50)
                .mapToObj(i -> new Security().symbol("Symbol" + i).currency(i % 2 == 0 ? Currency.CAD : Currency.USD))
                .collect(Collectors.toList());

        securities.forEach(security -> priceService.addPrice(security, bd(random.nextInt(500))));

        // 80% have an existing position in an account. The others don't
        List<Position> positions = random.ints(40, 0, 50)
                .mapToObj(i -> new Position()
                        .quantity(100L + random.nextLong() % 1000L)
                        .security(securities.get(i)))
                .collect(Collectors.toList());

        // 3 accounts is close to reality, two in CAD, one in USD
        Account[] accounts = {
                new Account()
                        .cash(bd(1_000_000))
                        .name("Account 1")
                        .currency(Currency.CAD),

                new Account()
                        .cash(bd(500_000))
                        .name("Account 2")
                        .currency(Currency.CAD),

                new Account()
                        .cash(bd(750_000))
                        .name("Account 3")
                        .currency(Currency.USD)
        };

        Arrays.stream(accounts).forEach(userDataService::addAccount);

        // Assign the positions randomly to the accounts
        positions.forEach(position -> {
            Account account = accounts[random.nextInt(accounts.length)];
            account.addPosition(position);
        });

        // 200 transactions seems right
        IntStream.range(0, 200)
                .mapToObj(i -> createTransaction(random, accounts, securities))
                .forEach(userDataService::addTransaction);
    }

    private Transaction createTransaction(Random random, Account[] accounts, List<Security> securities) {
        Account account = accounts[random.nextInt(3)];
        TransactionType type = TransactionType.values()[random.nextInt(TransactionType.values().length)];
        Transaction transaction = new Transaction();

        switch (type) {
            case buy:
                transaction
                        .amount(bd(1 + random.nextInt(1_000)).negate())
                        .quantity(1L + random.nextInt(100))
                        .fee(bd("7.5")); // fees are generally always the same
                break;
            case sell:
                transaction
                        .amount(bd(1 + random.nextInt(1_000)))
                        .quantity(-1L - random.nextInt(100))
                        .fee(bd("7.5"));
                break;
            case deposit:
                transaction.amount(bd(1 + random.nextInt(1_000)));
                break;
            case withdrawal:
                transaction.amount(bd(1 + random.nextInt(1_000)).negate());
                break;
        }

        return transaction
                .accountName(account.getName())
                .type(type)
                .currency(account.getCurrency()) // currency should be in the account currency in general
                .security(securities.get(random.nextInt(securities.size())))
                .tradeDate(lastYear.plusDays(random.nextInt(365)));
    }

    @Benchmark
    public ReportingDTO getReport() {
        return service.getReport(lastYear);
    }
}
