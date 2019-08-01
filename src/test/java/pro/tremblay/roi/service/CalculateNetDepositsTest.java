package pro.tremblay.roi.service;

import org.junit.Test;
import pro.tremblay.roi.domain.Currency;
import pro.tremblay.roi.domain.Transaction;
import pro.tremblay.roi.domain.TransactionType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.tremblay.roi.util.BigDecimalUtil.bd;

public class CalculateNetDepositsTest extends ReportingServiceTest {

    @Test
    public void noTransactions() {
        assertThat(reportingService.calculateNetDeposits(Collections.emptyList())).isEqualByComparingTo("0");
    }

    @Test
    public void noAmount() {
        List<Transaction> transactions = Arrays.asList(
                transaction().type(TransactionType.deposit),
                transaction().type(TransactionType.withdrawal),
                transaction().type(TransactionType.sell),
                transaction().type(TransactionType.buy));
        assertThat(reportingService.calculateNetDeposits(transactions)).isEqualByComparingTo("0");
    }

    @Test
    public void calculate() {
        List<Transaction> transactions = Arrays.asList(
                transaction().type(TransactionType.deposit).amount(bd(500)),
                transaction().type(TransactionType.withdrawal).amount(bd(-200)),
                transaction().type(TransactionType.sell).amount(bd(50)),
                transaction().type(TransactionType.buy).amount(bd(75)));
        assertThat(reportingService.calculateNetDeposits(transactions)).isEqualByComparingTo("300");
    }

    @Test
    public void changeRate() {
        List<Transaction> transactions = Arrays.asList(
                transaction().type(TransactionType.deposit).amount(bd(500)).currency(Currency.USD),
                transaction().type(TransactionType.withdrawal).amount(bd(-200)).currency(Currency.USD));
        assertThat(reportingService.calculateNetDeposits(transactions)).isEqualByComparingTo("450");
    }
    
}
