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
import java.math.RoundingMode;

public class Variation {
    private static final Variation NO_VARIATION = variation(BigDecimal.ZERO, BigDecimal.ZERO);

    /** Percentage of variation */
    private final BigDecimal percentage;
    /** Amount of variation */
    private final BigDecimal amount;

    public static Variation noVariation() {
        return NO_VARIATION;
    }

    public static Variation variation(BigDecimal percentage, BigDecimal amount) {
        return new Variation(percentage, amount);
    }

    public static Variation variationFromTotal(BigDecimal totalAmount, BigDecimal amount) {
        // If everything is zero, consider 0 percent. If total is zero and variation is not, consider all is variation so 100
        if(totalAmount.signum() == 0) {
            if(amount.signum() == 0) {
                return noVariation();
            }
            return variation(BigDecimal.ONE, amount);
        }

        // We need 4 decimals after the decimal point to allow us to have 2 decimals when presenting the numbers
        BigDecimal percentage = amount.divide(totalAmount, 4, RoundingMode.HALF_UP);
        return new Variation(percentage, amount);
    }

    private Variation(BigDecimal percentage, BigDecimal amount) {
        this.percentage = percentage;
        this.amount = amount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "amount: " + amount + " pct: " + percentage;
    }

    public Variation scale(int amountScale, int percentageScale) {
        return Variation.variation(
                percentage.setScale(percentageScale, RoundingMode.HALF_UP),
                amount.setScale(amountScale, RoundingMode.HALF_UP));
    }
}
