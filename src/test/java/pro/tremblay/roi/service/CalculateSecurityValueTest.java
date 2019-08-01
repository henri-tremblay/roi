package pro.tremblay.roi.service;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Test;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Position;
import pro.tremblay.roi.domain.Security;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.tremblay.roi.util.BigDecimalUtil.bd;

public class CalculateSecurityValueTest extends ReportingServiceTest {

    @Test
    public void noPositions() {
        assertThat(reportingService.calculateSecurityValue(now, Collections.emptyList(), new MultiKeyMap<>())).isEqualByComparingTo("0");
    }

    @Test
    public void positionWithoutAPrice() {
        List<Position> positions = Collections.singletonList(
                new Position().security(new Security().symbol(SYMBOL).currency(Currency.CAD))
        );
        assertThat(reportingService.calculateSecurityValue(now, positions, new MultiKeyMap<>())).isEqualByComparingTo("0");
    }

    @Test
    public void calculate() {
        List<Position> positions = Arrays.asList(
                new Position().security(new Security().symbol(SYMBOL).currency(Currency.CAD)).quantity(100L),
                new Position().security(new Security().symbol(SYMBOL).currency(Currency.USD)).quantity(200L)
        );

        MultiKeyMap<Object, BigDecimal> prices = new MultiKeyMap<>();
        prices.put(now, SYMBOL, Currency.CAD, bd(10));
        prices.put(now, SYMBOL, Currency.USD, bd(20));

        assertThat(reportingService.calculateSecurityValue(now, positions, prices)).isEqualByComparingTo("7000"); // 100x10 + 200x20x1.5
    }
}
