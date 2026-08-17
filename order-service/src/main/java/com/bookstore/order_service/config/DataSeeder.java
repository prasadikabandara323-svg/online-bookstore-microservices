package com.bookstore.order_service.config;

import com.bookstore.order_service.model.Order;
import com.bookstore.order_service.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final OrderRepository orderRepository;

    public DataSeeder(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Database එකේ Orders එකක්වත් නැත්නම් පමණක් auto-seed වේ
        if (orderRepository.count() == 0) {
            Order order1 = new Order();
            order1.setUserId(1L);
            order1.setTotalAmount(4700.00);
            order1.setStatus("CREATED");
            order1.setApiKeyValid(true);
            order1.setOrderDate(LocalDateTime.now());

            Order order2 = new Order();
            order2.setUserId(2L);
            order2.setTotalAmount(2500.00);
            order2.setStatus("PENDING");
            order2.setApiKeyValid(true);
            order2.setOrderDate(LocalDateTime.now());

            orderRepository.saveAll(List.of(order1, order2));
            System.out.println("✅ Order Service Data Seeded Successfully!");
        }
    }
}