package me.jamie.fullstack_payments_transaction_platform.repository;

import me.jamie.fullstack_payments_transaction_platform.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, String> {
}
