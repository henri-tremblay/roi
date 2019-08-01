package pro.tremblay.roi.domain;

/**
 * All supported currencies
 */
public enum Currency {
    CAD(1.0),
    USD(1.5);

    /** X-rate. Of course in real life this will not be hardcoded */
    private final double rate;

    Currency(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public double convertTo(Currency destination) {
        return rate / destination.rate;
    }
}
