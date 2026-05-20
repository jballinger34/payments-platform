package me.jamie.fullstack_payments_transaction_platform.data.request;

import me.jamie.fullstack_payments_transaction_platform.entity.Currency;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(@NotBlank String payerName, @NotBlank String payeeName, @NotNull BigDecimal amount, @NotNull Currency currency) {
}
