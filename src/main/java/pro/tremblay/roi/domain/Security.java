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
