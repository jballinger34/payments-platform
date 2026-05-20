package me.jamie.fullstack_payments_transaction_platform.controller;

import jakarta.validation.Valid;
import me.jamie.fullstack_payments_transaction_platform.data.dto.PaymentDto;
import me.jamie.fullstack_payments_transaction_platform.data.request.PaymentRequest;
import me.jamie.fullstack_payments_transaction_platform.entity.Payment;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;
import me.jamie.fullstack_payments_transaction_platform.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto createPayment(@Valid @RequestBody PaymentRequest paymentRequest){
        Payment payment = paymentService.createPayment(
                paymentRequest.payerName(), paymentRequest.payeeName(),
                paymentRequest.amount(), paymentRequest.currency());
        return PaymentDto.from(payment);
    }
    @GetMapping("/payments")
    public List<PaymentDto> getAllPayment(){
        List<Payment> payments = paymentService.getAllPayments();
        return payments.stream().map(PaymentDto::from).toList();
    }
    @GetMapping("/payments/{id}")
    public PaymentDto getPayment(@PathVariable String id){
        return PaymentDto.from(paymentService.getPayment(id));
    }
    @PutMapping("/payments/{id}/status")
    public PaymentDto updateStatus(@PathVariable String id, @RequestBody PaymentStatus status){
        Payment payment = paymentService.updateStatus(id, status);
        return PaymentDto.from(payment);
    }
    @GetMapping("/payments/search/status/{status}")
    public List<PaymentDto> getPaymentsByStatus(@PathVariable PaymentStatus status){
        List<Payment> paymentsWithStatus = paymentService.getPaymentsByStatus(status);
        return paymentsWithStatus.stream().map(PaymentDto::from).toList();
    }

    @GetMapping("/payments/search/payer/{payerName}")
    public List<PaymentDto> getPaymentsByPayerName(@PathVariable String payerName){
        List<Payment> paymentsForPayer = paymentService.getPaymentsByPayerName(payerName);
        return paymentsForPayer.stream().map(PaymentDto::from).toList();
    }


}
