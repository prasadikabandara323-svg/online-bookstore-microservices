package com.bookstore.order_service.controller;

import com.bookstore.order_service.model.Order;
import com.bookstore.order_service.service.ApiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Value("${app.api.key}")
    private String apiKey;

    private final ApiService apiService;

    public OrderController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "API is working securely!";
    }

    @PostMapping("/process")
    public String processOrder(@RequestBody String orderData) {
        return apiService.processData(orderData);
    }

    // 1. Create Order (POST)
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        Order savedOrder = apiService.createOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    // 2. Get All Orders (GET)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(apiService.getAllOrders());
    }

    // 3. Get Order By ID (GET - MongoDB String ID)
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        return apiService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Get Orders By User ID (GET)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(apiService.getOrdersByUserId(userId));
    }

    // 5. Update Order By ID (PUT - MongoDB String ID)
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable String id, @Valid @RequestBody Order order) {
        return apiService.updateOrder(id, order)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. Delete Order By ID (DELETE - MongoDB String ID)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        if (apiService.deleteOrder(id)) {
            return ResponseEntity.ok("Order deleted successfully!");
        }
        return ResponseEntity.notFound().build();
    }
}