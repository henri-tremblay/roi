package pro.tremblay.roi.util;

import java.math.BigDecimal;
import java.util.stream.Stream;

public final class BigDecimalUtil {

    public static BigDecimal BD_100 = new BigDecimal("100.00");

    public static BigDecimal bd(String val) { return val == null ? null : new BigDecimal(val); }

    public static BigDecimal bd(Long val) { return val == null ? null : BigDecimal.valueOf(val); }

    public static BigDecimal bd(Integer val) { return val == null ? null : BigDecimal.valueOf(val); }

    public static BigDecimal sum(Stream<BigDecimal> stream) {
        return stream.reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimalUtil() {}
}
