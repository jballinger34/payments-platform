package me.jamie.fullstack_payments_transaction_platform.kafka;

import me.jamie.fullstack_payments_transaction_platform.data.event.PaymentCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publishPaymentCreated(PaymentCreatedEvent event){
        kafkaTemplate.send("payments.created", event);
    }

}
