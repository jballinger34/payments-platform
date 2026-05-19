package me.jamie.fullstack_payments_transaction_platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Payment {

    @Id
    private String id;
    private String payerName;
    private String payeeName;
    private BigDecimal amount;
    private Currency currency;
    private PaymentStatus status;

    public Payment() {}

    public Payment(String payerName, String payeeName, BigDecimal amount, Currency currency) {
        this(String.valueOf(UUID.randomUUID()), payerName, payeeName, amount, currency, PaymentStatus.INITIATED);
    }

    public Payment(String id, String payerName, String payeeName, BigDecimal amount, Currency currency, PaymentStatus status) {
        this.id = id;
        this.payerName = payerName;
        this.payeeName = payeeName;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getPayerName() {
        return payerName;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        if(this.status == PaymentStatus.COMPLETED) throw new IllegalStateException("cannot change state of a completed payment");
        this.status = status;
    }
}
