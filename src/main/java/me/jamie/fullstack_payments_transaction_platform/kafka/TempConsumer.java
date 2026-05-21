package me.jamie.fullstack_payments_transaction_platform.kafka;

import me.jamie.fullstack_payments_transaction_platform.data.event.PaymentCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TempConsumer {

    @KafkaListener(
            topics = "payments.created",
            groupId = "payment-group"
    )
    public void listen(PaymentCreatedEvent event){
        System.out.println("Received payment event: " + event.toString());
    }
}
