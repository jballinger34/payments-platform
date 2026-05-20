package me.jamie.fullstack_payments_transaction_platform.data.request;

import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;

public record UpdateStatusRequest(PaymentStatus status) {
}
