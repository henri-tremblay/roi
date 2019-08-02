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

import pro.tremblay.roi.domain.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeRateService extends DependencyService {

    public ExchangeRateService() {
    }

    public ExchangeRateService(boolean isThrottling) {
        super(isThrottling);
    }

    /**
     * Give the change rate between the origin and the destination currency at a given date.
     *
     * @param origin current currency used
     * @param destination currency we want to go to
     * @param date date of the rate
     * @return change rate
     */
    public BigDecimal getExchangeRate(Currency origin, Currency destination, LocalDate date) {
        return BigDecimal.valueOf(origin.convertTo(destination));
    }
}
