package com.bookstore.order_service.service;

import com.bookstore.order_service.model.Order;
import com.bookstore.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);
    private final OrderRepository orderRepository;

    public ApiService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String processData(String inputData) {
        if (inputData == null || inputData.isEmpty()) {
            return "No data provided!";
        }
        return "Data processed successfully: " + inputData.toUpperCase();
    }

    // --- ORDER CRUD OPERATIONS FOR MONGODB ---

    // 1. Create Order
    public Order createOrder(Order order) {
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }
        log.info("Creating new order for User ID: {}", order.getUserId());
        return orderRepository.save(order);
    }

    // 2. Get All Orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 3. Get Order By ID (MongoDB String ID)
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    // 4. Get Orders By User ID
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // 5. Update Order (MongoDB String ID)
    public Optional<Order> updateOrder(String id, Order updatedOrder) {
        return orderRepository.findById(id).map(existingOrder -> {
            if (updatedOrder.getUserId() != null) existingOrder.setUserId(updatedOrder.getUserId());
            if (updatedOrder.getTotalAmount() != null) existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
            if (updatedOrder.getStatus() != null) existingOrder.setStatus(updatedOrder.getStatus());
            log.info("Updating order ID: {}", id);
            return orderRepository.save(existingOrder);
        });
    }

    // 6. Delete Order (MongoDB String ID)
    public boolean deleteOrder(String id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            log.info("Deleted order with ID: {}", id);
            return true;
        }
        log.warn("Failed to delete. Order ID {} not found", id);
        return false;
    }

    public Object getCartByUserId(Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCartByUserId'");
    }
    public Object addToCart(Map<String, Object> itemData) {
    return itemData;
}
public void clearCartByUserId(Long userId) {
    // Cart එක clear කරන logic එක (උදා: repository.deleteByUserId(userId);)
}
}