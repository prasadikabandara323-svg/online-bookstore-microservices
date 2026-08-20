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

    // --- ORDER CRUD OPERATIONS FOR MONGODB (unchanged) ---

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

    // --- CART OPERATIONS (real MongoDB persistence, bookId as String) ---

    public Object getCartByUserId(Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);

        double total = items.stream()
                .mapToDouble(i -> (i.getPrice() != null ? i.getPrice() : 0) *
                        (i.getQuantity() != null ? i.getQuantity() : 1))
                .sum();

        Map<String, Object> cart = new HashMap<>();
        cart.put("items", items);
        cart.put("totalAmount", total);
        return cart;
    }

    public Object addToCart(Map<String, Object> itemData) {
        Long userId = Long.valueOf(itemData.get("userId").toString());
        String bookId = itemData.get("bookId").toString(); // 👈 Long.valueOf() ඉවත් කළා - book-catalog ID එක String
        String bookTitle = itemData.get("bookTitle") != null ? itemData.get("bookTitle").toString() : null;
        Double price = itemData.get("price") != null ? Double.valueOf(itemData.get("price").toString()) : 0.0;
        Integer quantity = itemData.get("quantity") != null
                ? Integer.valueOf(itemData.get("quantity").toString()) : 1;

        List<CartItem> existingItems = cartRepository.findByUserId(userId);
        Optional<CartItem> existing = existingItems.stream()
                .filter(i -> bookId.equals(i.getBookId()))
                .findFirst();

        CartItem saved;
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            saved = cartRepository.save(item);
        } else {
            CartItem newItem = new CartItem(null, userId, bookId, bookTitle, quantity, price);
            saved = cartRepository.save(newItem);
        }

        log.info("Added/updated cart item for userId={}, bookId={}, price={}", userId, bookId, price);
        return saved;
    }

    public void clearCartByUserId(Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(items);
        log.info("Cleared cart for userId={}", userId);
    }
}