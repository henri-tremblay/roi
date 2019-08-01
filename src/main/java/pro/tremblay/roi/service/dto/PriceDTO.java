package pro.tremblay.roi.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceDTO {
    private final LocalDate date;
    private final BigDecimal value;

    public static PriceDTO of(LocalDate date, BigDecimal value) {
        return new PriceDTO(date, value);
    }

    private PriceDTO(LocalDate date, BigDecimal value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PriceDTO{" +
                "date=" + date +
                ", value=" + value +
                '}';
    }
}
