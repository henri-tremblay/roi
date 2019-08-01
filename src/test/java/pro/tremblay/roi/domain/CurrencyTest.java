package pro.tremblay.roi.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyTest {

    @Test
    public void convertToCadToCad() {
        assertThat(Currency.CAD.convertTo(Currency.CAD)).isEqualTo(1);
    }

    @Test
    public void convertToCadToUsd() {
        assertThat(Currency.CAD.convertTo(Currency.USD)).isEqualTo(1.0/1.5);
    }

    @Test
    public void convertToUsdToCad() {
        assertThat(Currency.USD.convertTo(Currency.CAD)).isEqualTo(1.5/1.0);
    }
}
