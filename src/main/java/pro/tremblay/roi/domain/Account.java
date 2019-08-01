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
