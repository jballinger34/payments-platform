package me.jamie.fullstack_payments_transaction_platform.data.event;

import me.jamie.fullstack_payments_transaction_platform.entity.Currency;
import me.jamie.fullstack_payments_transaction_platform.entity.Payment;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentCreatedEvent(String id, String payerName, String payeeName, BigDecimal amount, Currency currency, PaymentStatus status) {

    public static PaymentCreatedEvent from(Payment p){
        return new PaymentCreatedEvent(p.getId(), p.getPayerName(), p.getPayeeName(), p.getAmount(), p.getCurrency(), p.getStatus());
    }
}
