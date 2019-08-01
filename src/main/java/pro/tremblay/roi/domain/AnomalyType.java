package pro.tremblay.roi.domain;

public enum AnomalyType {
    /** Initial value is a negative number on cash or position. Which is impossible */
    NEGATIVE_INITIAL_VALUE,
    /** No price is available for the position */
    NO_PRICE_AVAILABLE,
    /** Account name on a transaction doesn't correspond to any account */
    ACCOUNT_NOT_FOUND,
    /** Some positions have no symbol and so can't be priced */
    NOT_PRICEABLE_POSITION
}
