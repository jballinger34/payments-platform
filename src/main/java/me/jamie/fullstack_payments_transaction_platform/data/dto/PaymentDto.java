package me.jamie.fullstack_payments_transaction_platform.data.dto;

import me.jamie.fullstack_payments_transaction_platform.entity.Currency;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentDto(String payerName, String payeeName, BigDecimal amount, Currency currency, PaymentStatus status){
}
