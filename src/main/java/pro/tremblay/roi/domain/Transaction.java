package pro.tremblay.roi.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity. A transaction to buy or sell a security
 */
public class Transaction implements Entity {
    /** Account on which the transaction was made. It will match an {@link Account} name */
    private String accountName;
    /** Security involved in the transaction. Can be null for transactions not involving securities */
    private Security security;
    /** Date at which the transaction occured */
    private LocalDate tradeDate;
    /** Currency of the fee and amount on the transaction */
    private Currency currency;
    /** Type of transaction */
    private TransactionType type;
    /** Amount in cash involved in the transaction. Fees are included in this amount. In the transaction currency. Will be negative if cash was withdrawn from the account */
    private BigDecimal amount;
    /** Fees charged for processing the transaction, in the transaction currency */
    private BigDecimal fee;
    /** Quantity of security bought or sold. Will be a negative number of sold */
    private Long quantity;

    public String getAccountName() {
        return accountName;
    }

    public Transaction accountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public Security getSecurity() {
        return security;
    }

    public Transaction security(Security security) {
        this.security = security;
        return this;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public Transaction tradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Transaction currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public TransactionType getType() {
        return type;
    }

    public Transaction type(TransactionType type) {
        this.type = type;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Transaction amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Transaction quantity(Long quantity) {
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public Transaction fee(BigDecimal fee) {
        this.fee = fee;
        return this;
    }
}
