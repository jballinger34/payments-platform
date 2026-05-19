package me.jamie.fullstack_payments_transaction_platform.service;

import me.jamie.fullstack_payments_transaction_platform.exception.PaymentNotFoundException;
import me.jamie.fullstack_payments_transaction_platform.model.Currency;
import me.jamie.fullstack_payments_transaction_platform.model.Payment;
import me.jamie.fullstack_payments_transaction_platform.model.PaymentStatus;
import me.jamie.fullstack_payments_transaction_platform.repository.PaymentRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private PaymentRepo repo;

    public PaymentService(PaymentRepo repo) {
        this.repo = repo;
    }

    public Payment createPayment(String payerName, String payeeName, BigDecimal amount, Currency currency){
        throw new UnsupportedOperationException("not implemented yet");
    }
    public List<Payment> getAllPayments(){
        throw new UnsupportedOperationException("not implemented yet");
    }
    public Payment getPayment(String id) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented yet");
    }
    public Payment updateStatus(String id, PaymentStatus status){
        throw new UnsupportedOperationException("not implemented yet");
    }
    public List<Payment> getPaymentsByStatus(PaymentStatus status){
        throw new UnsupportedOperationException("not implemented yet");
    }
    public List<Payment> getPaymentsByPayerName(String payerName){
        throw new UnsupportedOperationException("not implemented yet");
    }
}
