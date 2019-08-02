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

import org.junit.Before;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Transaction;

import java.time.LocalDate;

public abstract class ReportingServiceTest {

    protected static final String ACCOUNT_NAME = "123";
    protected static final String SYMBOL = "XXX";

    protected final UserDataService userDataService = new UserDataService();
    protected final PriceService priceService = new PriceService();
    protected final ExchangeRateService exchangeRateService = new ExchangeRateService();
    protected final MessageService messageService = new MessageService();

    protected ReportingService reportingService = createReportingService();

    protected LocalDate now = LocalDate.now();
    protected LocalDate oneYearAgo = now.minusYears(1);
    protected LocalDate sixMonthsAgo = now.minusMonths(6);

    protected ReportingService createReportingService() {
        return new ReportingService(userDataService, priceService, exchangeRateService, messageService);
    }

    protected Transaction transaction() {
        return new Transaction().currency(Currency.CAD).tradeDate(LocalDate.now());
    }
}
