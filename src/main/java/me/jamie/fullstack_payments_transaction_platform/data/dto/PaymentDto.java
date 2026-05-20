package me.jamie.fullstack_payments_transaction_platform.data.dto;

import me.jamie.fullstack_payments_transaction_platform.entity.Currency;
import me.jamie.fullstack_payments_transaction_platform.entity.Payment;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentDto(String id, String payerName, String payeeName, BigDecimal amount, Currency currency, PaymentStatus status){

    public static PaymentDto from(Payment payment){
        return new PaymentDto(payment.getId(), payment.getPayerName(), payment.getPayeeName(), payment.getAmount(), payment.getCurrency(), payment.getStatus());
    }
}
