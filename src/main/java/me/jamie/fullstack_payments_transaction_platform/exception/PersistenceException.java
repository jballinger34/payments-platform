package me.jamie.fullstack_payments_transaction_platform.exception;

public class PersistenceException extends RuntimeException {
    public PersistenceException(String message) {
        super(message);
    }
}
