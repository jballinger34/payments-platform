package me.jamie.fullstack_payments_transaction_platform.data.event;


import me.jamie.fullstack_payments_transaction_platform.entity.Currency;
import me.jamie.fullstack_payments_transaction_platform.entity.Payment;
import me.jamie.fullstack_payments_transaction_platform.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentUpdatedStatusEvent(String paymentId, String payerName, String payeeName,
                                        BigDecimal amount, Currency currency,
                                        PaymentStatus oldStatus, PaymentStatus newStatus) {

    //takes oldstatus and newstatus as explicit argument
    //could take one from payment but this may be confusing becuase youd
    //have to make sure everything is done in correct order
    public static PaymentUpdatedStatusEvent from(Payment payment, PaymentStatus oldStatus, PaymentStatus newStatus) {
        return new PaymentUpdatedStatusEvent(payment.getId(), payment.getPayerName(), payment.getPayeeName(), payment.getAmount(), payment.getCurrency(), oldStatus, newStatus);
    }
}