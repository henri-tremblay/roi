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

public class CalculateCommissionTest extends ReportingServiceTest {

    @Test
    public void noTransactions() {
        assertThat(reportingService.calculateCommission(Collections.emptyList())).isEqualByComparingTo("0");
    }

    @Test
    public void noFee() {
        List<Transaction> transactions = Collections.singletonList(transaction().fee(null));
        assertThat(reportingService.calculateCommission(transactions)).isEqualByComparingTo("0");
    }

    @Test
    public void noCurrency() {
        List<Transaction> transactions = Collections.singletonList(transaction().fee(bd(10)).currency(null));
        assertThat(reportingService.calculateCommission(transactions)).isEqualByComparingTo("0");
    }

    @Test
    public void calculate() {
        List<Transaction> transactions = Arrays.asList(
                transaction().type(TransactionType.deposit).fee(bd(500)),
                transaction().type(TransactionType.withdrawal).fee(bd(200)),
                transaction().type(TransactionType.sell).fee(bd(50)),
                transaction().type(TransactionType.buy).fee(bd(75)));
        assertThat(reportingService.calculateCommission(transactions)).isEqualByComparingTo("825");
    }

    @Test
    public void changeRate() {
        List<Transaction> transactions = Collections.singletonList(transaction().fee(bd(10)).currency(Currency.USD));
        assertThat(reportingService.calculateCommission(transactions)).isEqualByComparingTo("15");
    }
    
}
