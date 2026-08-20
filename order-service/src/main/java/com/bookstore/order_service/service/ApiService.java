package com.bookstore.order_service.service;

import com.bookstore.order_service.model.CartItem;
import com.bookstore.order_service.model.Order;
import com.bookstore.order_service.repository.CartRepository;
import com.bookstore.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public ApiService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public String processData(String inputData) {
        if (inputData == null || inputData.isEmpty()) {
            return "No data provided!";
        }
        return "Data processed successfully: " + inputData.toUpperCase();
    }

    // --- CART OPERATIONS FOR MONGODB ---

    public Object getCartByUserId(Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        
        double totalAmount = items.stream()
                .mapToDouble(item -> (item.getPrice() != null ? item.getPrice() : 0.0) * (item.getQuantity() != null ? item.getQuantity() : 1))
                .sum();

        Map<String, Object> cartResponse = new HashMap<>();
        cartResponse.put("userId", userId);
        cartResponse.put("items", items);
        cartResponse.put("totalAmount", totalAmount);

        return cartResponse;
    }

    public Object addToCart(Map<String, Object> itemData) {
        Long userId = Long.valueOf(itemData.get("userId").toString());
        Long bookId = Long.valueOf(itemData.get("bookId").toString());
        String bookTitle = itemData.containsKey("bookTitle") ? itemData.get("bookTitle").toString() : "Unknown Title";
        Double price = Double.valueOf(itemData.get("price").toString());
        Integer quantity = itemData.containsKey("quantity") ? Integer.valueOf(itemData.get("quantity").toString()) : 1;

        Optional<CartItem> existingItemOpt = cartRepository.findByUserIdAndBookId(userId, bookId);

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setBookId(bookId);
            cartItem.setBookTitle(bookTitle);
            cartItem.setPrice(price);
            cartItem.setQuantity(quantity);
        }

        log.info("Saving Cart Item to MongoDB for User ID: {}, Book ID: {}", userId, bookId);
        cartRepository.save(cartItem);

        return getCartByUserId(userId);
    }

    public void clearCartByUserId(Long userId) {
        log.info("Clearing Cart for User ID: {}", userId);
        cartRepository.deleteByUserId(userId);
    }

    // --- ORDER CRUD OPERATIONS FOR MONGODB ---

    public Order createOrder(Order order) {
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }
        log.info("Creating new order for User ID: {}", order.getUserId());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> updateOrder(String id, Order updatedOrder) {
        return orderRepository.findById(id).map(existingOrder -> {
            if (updatedOrder.getUserId() != null) existingOrder.setUserId(updatedOrder.getUserId());
            if (updatedOrder.getTotalAmount() != null) existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
            if (updatedOrder.getStatus() != null) existingOrder.setStatus(updatedOrder.getStatus());
            log.info("Updating order ID: {}", id);
            return orderRepository.save(existingOrder);
        });
    }

    public boolean deleteOrder(String id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            log.info("Deleted order with ID: {}", id);
            return true;
        }
        log.warn("Failed to delete. Order ID {} not found", id);
        return false;
    }
}