package pro.tremblay.roi.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalUtilTest {

    @Test
    public void bd_longNull() {
        assertThat(BigDecimalUtil.bd((Long) null)).isNull();
    }

    @Test
    public void bd_longValue() {
        assertThat(BigDecimalUtil.bd(3L)).isEqualTo(BigDecimal.valueOf(3L));
    }

    @Test
    public void bd_intValue() {
        assertThat(BigDecimalUtil.bd(3)).isEqualTo(BigDecimal.valueOf(3L));
    }

    @Test
    public void bd_stringNull() {
        assertThat(BigDecimalUtil.bd((String) null)).isNull();
    }

    @Test
    public void bd_stringValue() {
        assertThat(BigDecimalUtil.bd("10.1234")).isEqualTo(new BigDecimal("10.1234"));
    }

    @Test
    public void sum() {
        Stream<BigDecimal> number = Stream.of(BigDecimalUtil.bd("1.1"), BigDecimalUtil.bd("2.2"), BigDecimalUtil.bd("3.3"));
        assertThat(BigDecimalUtil.sum(number)).isEqualTo(BigDecimalUtil.bd("6.6"));
    }

    @Test
    public void sum_empty() {
        assertThat(BigDecimalUtil.sum(Stream.empty())).isEqualTo(BigDecimal.ZERO);
    }
}
