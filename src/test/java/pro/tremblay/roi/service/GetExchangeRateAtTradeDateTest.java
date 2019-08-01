package pro.tremblay.roi.service;

import org.junit.Test;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Transaction;
import pro.tremblay.roi.domain.TransactionType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.tremblay.roi.util.BigDecimalUtil.bd;

public class GetExchangeRateAtTradeDateTest extends ReportingServiceTest {

    @Test
    public void noCurrency() {
        Transaction transaction = transaction().currency(null);
        assertThat(reportingService.getExchangeRateAtTradeDate(transaction)).isEqualByComparingTo("0");
    }

    @Test
    public void noTradeDate() {
        Transaction transaction = transaction().tradeDate(null);
        assertThat(reportingService.getExchangeRateAtTradeDate(transaction)).isEqualByComparingTo("0");
    }

    @Test
    public void allGood() {
        Transaction transaction = transaction();
        assertThat(reportingService.getExchangeRateAtTradeDate(transaction)).isEqualByComparingTo("1");
    }
}
