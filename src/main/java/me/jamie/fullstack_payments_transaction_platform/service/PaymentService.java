package me.jamie.fullstack_payments_transaction_platform.service;

import me.jamie.fullstack_payments_transaction_platform.data.event.PaymentCreatedEvent;
import me.jamie.fullstack_payments_transaction_platform.exception.PaymentNotFoundException;
import me.jamie.fullstack_payments_transaction_platform.entity.Currency;
import me.jamie.fullstack_payments_transaction_platform.entity.Payment;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;
import me.jamie.fullstack_payments_transaction_platform.kafka.PaymentEventProducer;
import me.jamie.fullstack_payments_transaction_platform.repository.PaymentRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepo repo;
    private final PaymentEventProducer producer;

    public PaymentService(PaymentRepo repo, PaymentEventProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    public Payment createPayment(String payerName, String payeeName, BigDecimal amount, Currency currency){
        if(payeeName == null || payerName == null) throw new IllegalArgumentException("payer and payee must not be null");
        if(currency == null) throw new IllegalArgumentException("currency must not be null");

        if(amount == null) throw new IllegalArgumentException("amount must not be null");
        if(amount.scale() > 2) throw new IllegalArgumentException("amount cannot have more than 2 decimal places");
        if(amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount must be greater than 0");

        Payment savedPayment = repo.save(new Payment(payerName, payeeName, amount, currency));

        PaymentCreatedEvent event = PaymentCreatedEvent.from(savedPayment);
        producer.publishPaymentCreated(event);

        return savedPayment;
    }
    public List<Payment> getAllPayments(){
        return repo.findAll();
    }
    public Payment getPayment(String id) throws UnsupportedOperationException {
        if(id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null or blank");

        return repo.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }
    public Payment updateStatus(String id, PaymentStatus status){
        if(id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null or blank");
        if(status == null) throw new IllegalArgumentException("status cannot be null");
        Payment payment = getPayment(id);
        payment.setStatus(status);

        repo.save(payment);
        return payment;
    }
    public List<Payment> getPaymentsByStatus(PaymentStatus status){
        if(status == null) throw new IllegalArgumentException("status cannot be null");
        return getAllPayments().stream().filter(payment -> payment.getStatus().equals(status)).toList();
    }
    public List<Payment> getPaymentsByPayerName(String payerName){
        if(payerName == null) throw new IllegalArgumentException("payer name cannot be null");
        return getAllPayments().stream().filter(payment -> payment.getPayerName().equals(payerName)).toList();
    }
}
