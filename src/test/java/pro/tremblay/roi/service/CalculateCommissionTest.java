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

import org.junit.Test;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Transaction;
import pro.tremblay.roi.domain.TransactionType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.tremblay.roi.util.BigDecimalUtil.bd;

public class CalculateCommissionTest extends ReportingServiceTest {

    @Test
    public void noTransactions() {
        assertThat(reportingService.calculateCommission(Collections.emptyList())).isEqualByComparingTo("0");
    }

    @Test
    public void noFee() {
        List<Transaction> transactions = Collections.singletonList(transaction().fee(null));
        assertThat(reportingService.calculateCommission(transactions)).isEqualByComparingTo("0");
    }

    @Test
    public void noCurrency() {
        List<Transaction> transactions = Collections.singletonList(transaction().fee(bd(10)).currency(null));
        assertThat(reportingService.calculateCommission(transactions)).isEqualByComparingTo("0");
    }

    @Test
    public void calculate() {
        List<Transaction> transactions = Arrays.asList(
                transaction().type(TransactionType.deposit).fee(bd(500)),
                transaction().type(TransactionType.withdrawal).fee(bd(200)),
                transaction().type(TransactionType.sell).fee(bd(50)),
                transaction().type(TransactionType.buy).fee(bd(75)));
        assertThat(reportingService.calculateCommission(transactions)).isEqualByComparingTo("825");
    }

    @Test
    public void changeRate() {
        List<Transaction> transactions = Collections.singletonList(transaction().fee(bd(10)).currency(Currency.USD));
        assertThat(reportingService.calculateCommission(transactions)).isEqualByComparingTo("15");
    }
    
}
