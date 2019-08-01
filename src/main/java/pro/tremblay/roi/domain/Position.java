package pro.tremblay.roi.domain;

/**
 * A security position held by an account.
 */
public class Position implements Entity {
    /** Security of the position */
    private Security security;
    /** Quantity of the position held */
    private Long quantity;

    public Position security(Security security) {
        this.security = security;
        return this;
    }

    public Currency getCurrency() {
        return security.getCurrency();
    }

    public Security getSecurity() {
        return security;
    }

    public Position quantity(Long quantity) {
        this.quantity = quantity;
        return this;
    }

    public Long getQuantity() {
        return quantity;
    }

}
