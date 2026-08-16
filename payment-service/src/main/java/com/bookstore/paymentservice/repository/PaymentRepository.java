package com.bookstore.paymentservice.repository;

import com.bookstore.paymentservice.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByUserId(String userId);
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByPaymentMethod(String paymentMethod);
}

