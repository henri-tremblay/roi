package pro.tremblay.roi.service.dto;

import pro.tremblay.roi.domain.Anomaly;
import pro.tremblay.roi.domain.AnomalyType;
import pro.tremblay.roi.domain.Variation;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pro.tremblay.roi.domain.Anomaly.*;

public class ReportingDTO {

    /** Daily account values on the period */
    private List<PriceDTO> valuePerDay;

    /** Initial value of the account. It is the same value as the first entry of {@link #valuePerDay} */
    private BigDecimal initial;

    /** Current value of the account. It is the same value as the last entry of {@link #valuePerDay} */
    private BigDecimal current;

    /** Net variation on the period including deposits and withdrawal */
    private Variation netVariation;

    /** Net amount of money added to the account during the period (money withdrawn is subtracted) */
    private BigDecimal netDeposits;

    /** Fees paid during the period */
    private BigDecimal fee;

    private Set<Anomaly> anomalies = new HashSet<>(0);

    public Set<Anomaly> getAnomalies() {
        return Collections.unmodifiableSet(anomalies);
    }

    public List<PriceDTO> getValuePerDay() {
        return valuePerDay;
    }

    public BigDecimal getInitial() {
        return initial;
    }

    public BigDecimal getCurrent() {
        return current;
    }

    public BigDecimal getNetDeposits() {
        return netDeposits;
    }

    public Variation getNetVariation() {
        return netVariation;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public void setValuePerDay(List<PriceDTO> valuePerDay) {
        this.valuePerDay = valuePerDay;
    }

    public void setInitial(BigDecimal initial) {
        this.initial = initial;
    }

    public void setCurrent(BigDecimal current) {
        this.current = current;
    }

    public void setNetDeposits(BigDecimal netDeposits) {
        this.netDeposits = netDeposits;
    }

    public void setNetVariation(Variation netVariation) {
        this.netVariation = netVariation;
    }

    public void addAnomaly(AnomalyType anomalyType, String message) {
        anomalies.add(anomaly(anomalyType, message));
    }
}
