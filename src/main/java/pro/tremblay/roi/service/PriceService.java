package pro.tremblay.roi.service;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.tuple.Pair;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Position;
import pro.tremblay.roi.domain.Security;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PriceService extends DependencyService {

    private final Collection<String> listOfErrors = new HashSet<>();
    private final Map<Security, BigDecimal> prices = new HashMap<>();

    public PriceService() {
    }

    public PriceService(boolean isThrottling) {
        super(isThrottling);
    }

    //
    // Methods to call during test to record a fake behavior
    //

    public void addError(String symbol) {
        listOfErrors.add(symbol);
    }

    public void addPrice(Security security, BigDecimal price) {
        prices.put(security, price);
    }

    ///////////////////////

    /**
     * All the prices for every position in the date range.
     *
     * @param positions list of positions we want prices for
     * @param firstDayOfPeriod first day for which we want a price
     * @param lastDay last day for which we want a price
     * @return a pair with a multimap of (date, symbol, currency) as the key and the price as the value,
     * and a list of error messages for every position we haven't found a price for
     */
    public Pair<MultiKeyMap<Object, BigDecimal>, Collection<String>> getPricesForPositions(List<Position> positions, LocalDate firstDayOfPeriod, LocalDate lastDay) {
        throttle(100);

        MultiKeyMap<Object, BigDecimal> allPrices = new MultiKeyMap<>();

        positions.forEach(position  -> {
            Security security = position.getSecurity();
            Currency currency = security.getCurrency();
            String symbol = security.getSymbol();

            BigDecimal price = prices.get(security);

            LocalDate workDate = firstDayOfPeriod;
            while (workDate.isBefore(lastDay.plusDays(1))) {
                allPrices.put(workDate, symbol, currency, price);
                workDate = workDate.plusDays(1);
            }
        });

        return Pair.of(allPrices, listOfErrors);
    }
}
