package me.jamie.fullstack_payments_transaction_platform.data.request;

import me.jamie.fullstack_payments_transaction_platform.entity.Currency;

import java.math.BigDecimal;

public record PaymentRequest(String payerName, String payeeName, BigDecimal amount, Currency currency) {
}
