package com.bookstore.paymentservice.config;

import com.bookstore.paymentservice.model.Payment;
import com.bookstore.paymentservice.repository.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(PaymentRepository paymentRepository) {
        return args -> {
            // Database එකේ දැනට Data නැත්නම් පමණක් Sample Data ඇතුළත් කරයි
            if (paymentRepository.count() == 0) {
                
                Payment p1 = new Payment();
                p1.setUserId("USER-101");
                p1.setOrderId("ORDER-1001");
                p1.setCustomerName("Kamal Perera");
                p1.setContactNo("0771234567");
                p1.setDescription("Payment for Order #1001");
                p1.setAmount(2500.00);
                p1.setPaymentMethod("CARD");
                p1.setStatus("SUCCESS");
                p1.setTimestamp(LocalDateTime.now().minusDays(2));

                Payment p2 = new Payment();
                p2.setUserId("USER-102");
                p2.setOrderId("ORDER-1002");
                p2.setCustomerName("Nimal Silva");
                p2.setContactNo("0719876543");
                p2.setDescription("Payment for Order #1002");
                p2.setAmount(1800.50);
                p2.setPaymentMethod("CASH_ON_DELIVERY");
                p2.setStatus("PENDING");
                p2.setTimestamp(LocalDateTime.now().minusHours(5));

                Payment p3 = new Payment();
                p3.setUserId("USER-103");
                p3.setOrderId("ORDER-1003");
                p3.setCustomerName("Sunil Fernando");
                p3.setContactNo("0754443322");
                p3.setDescription("Payment for Order #1003");
                p3.setAmount(4200.00);
                p3.setPaymentMethod("CARD");
                p3.setStatus("FAILED");
                p3.setTimestamp(LocalDateTime.now().minusMinutes(30));

                paymentRepository.saveAll(List.of(p1, p2, p3));
                System.out.println("✅ Payment Service Data Seeding Completed Successfully!");
            } else {
                System.out.println("ℹ️ Database already contains data. Skipping Seeding.");
            }
        };
    }
}