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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An account with cash and security positions.
 */
public class Account implements Entity {

    /** Name to identify the account. Is unique throughout the accounts */
    private String name;

    /** Currency used by the account and currency of the cash on the account */
    private Currency currency;

    /** Cash available on the account */
    private BigDecimal cash;

    /** Security positions of the account */
    private List<Position> positions = new ArrayList<>();

    public List<Position> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    public Account addPosition(Position position) {
        positions.add(position);
        return this;
    }

    public Account currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    public Account name(String name) {
        this.name = name;
        return this;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public Account cash(BigDecimal cash) {
        this.cash = cash;
        return this;
    }
}
