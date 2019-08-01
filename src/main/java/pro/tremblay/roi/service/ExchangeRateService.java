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
