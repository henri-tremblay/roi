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
import pro.tremblay.roi.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.tremblay.roi.util.BigDecimalUtil.bd;

public class CalculateCashValueTest extends ReportingServiceTest {

    @Test
    public void noPositions() {
        assertThat(reportingService.calculateCashValue(now, Collections.emptyMap())).isEqualByComparingTo("0");
    }

    @Test
    public void calculate() {
        Map<Currency, BigDecimal> cashPositions = new HashMap<>();
        cashPositions.put(Currency.CAD, bd(100));
        cashPositions.put(Currency.USD, bd(200));
        assertThat(reportingService.calculateCashValue(now, cashPositions)).isEqualByComparingTo("400");
    }

}
