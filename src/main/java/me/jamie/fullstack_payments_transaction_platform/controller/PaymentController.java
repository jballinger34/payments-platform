package me.jamie.fullstack_payments_transaction_platform.controller;

import me.jamie.fullstack_payments_transaction_platform.data.dto.PaymentDto;
import me.jamie.fullstack_payments_transaction_platform.data.request.PaymentRequest;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;
import me.jamie.fullstack_payments_transaction_platform.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    @PostMapping("/payments")
    public void createPayment(@RequestBody PaymentRequest paymentRequest){
        throw new UnsupportedOperationException("not implemented yet");
    }
    @GetMapping("/payments")
    public List<PaymentDto> getAllPayment(){
        throw new UnsupportedOperationException("not implemented yet");
    }
    @GetMapping("/payments/{id}")
    public PaymentDto getPayment(@PathVariable String id){
        throw new UnsupportedOperationException("not implemented yet");
    }
    @PutMapping("/payments/{id}/status")
    public void getPayment(@RequestBody PaymentStatus status){
        throw new UnsupportedOperationException("not implemented yet");
    }
    @GetMapping("/payments/search/status/{status}")
    public List<PaymentDto> getPaymentsByStatus(@PathVariable PaymentStatus status){
        throw new UnsupportedOperationException("not implemented yet");
    }

    @GetMapping("/payments/search/status/{payerName}")
    public List<PaymentDto> getPaymentsByPayerName(@PathVariable String payerName){
        throw new UnsupportedOperationException("not implemented yet");
    }


}
