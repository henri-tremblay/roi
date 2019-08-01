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
