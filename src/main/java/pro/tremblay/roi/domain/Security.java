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
package pro.tremblay.roi.domain;

import java.util.Objects;

/**
 * Security aka financial instrument.
 */
public class Security implements Entity {

    /** Symbol of the security */
    private String symbol;
    /** Currency in which the security is traded (and so the currency of the price we obtain from the {@link pro.tremblay.roi.service.PriceService} */
    private Currency currency;

    public Security symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Security currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Security security = (Security) o;
        return Objects.equals(symbol, security.symbol) &&
                currency == security.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, currency);
    }
}
