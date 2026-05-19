package me.jamie.fullstack_payments_transaction_platform.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String id) {
        super("Payment " + id + " not found.");
    }
}
