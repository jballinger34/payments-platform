package me.jamie.fullstack_payments_transaction_platform.kafka;

import me.jamie.fullstack_payments_transaction_platform.data.event.PaymentCreatedEvent;
import me.jamie.fullstack_payments_transaction_platform.data.event.PaymentUpdatedStatusEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publishPaymentCreated(PaymentCreatedEvent event){
        kafkaTemplate.send("payments.created", event);
    }
    public void publishPaymentStatusUpdated(PaymentUpdatedStatusEvent event){
        kafkaTemplate.send("payments.updated-status", event);
    }

}
