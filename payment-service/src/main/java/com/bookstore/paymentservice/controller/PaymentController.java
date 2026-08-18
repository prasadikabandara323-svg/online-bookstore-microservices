package com.bookstore.paymentservice.controller;

import com.bookstore.paymentservice.model.Payment;
import com.bookstore.paymentservice.repository.PaymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // 1. Endpoint: Create New Payment (POST)
    // "/create" saha "/process" dekama frontend eken call karanna puluwan
    // widihata map kara thiyenne - client-app/payment.html eken
    // "/payments/process" ekata request ekak yawanawa nisa.
    @PostMapping({"/create", "/process"})
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        payment.setTimestamp(LocalDateTime.now());
        if (payment.getStatus() == null) {
            payment.setStatus("COMPLETED");
        }
        Payment savedPayment = paymentRepository.save(payment);
        return ResponseEntity.ok(savedPayment);
    }

    // 2. Endpoint: Find Payment by Order ID (GET)
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable String orderId) {
        Optional<Payment> payment = paymentRepository.findByOrderId(orderId);
        return payment.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. Endpoint: Filter Payments by Payment Method (GET) - e.g., CARD, BANK_TRANSFER
    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable String paymentMethod) {
        List<Payment> payments = paymentRepository.findByPaymentMethod(paymentMethod);
        return ResponseEntity.ok(payments);
    }

    // 4. Endpoint: Update Payment Status Dynamically (PUT)
    @PutMapping("/update-status/{id}")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable String id, @RequestParam String status) {
        Optional<Payment> existingPayment = paymentRepository.findById(id);
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            payment.setStatus(status);
            Payment updatedPayment = paymentRepository.save(payment);
            return ResponseEntity.ok(updatedPayment);
        }
        return ResponseEntity.notFound().build();
    }
}