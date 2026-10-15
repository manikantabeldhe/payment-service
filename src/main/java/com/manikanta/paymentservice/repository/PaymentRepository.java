package com.manikanta.paymentservice.repository;

import com.manikanta.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByEventId(String eventId);
}
