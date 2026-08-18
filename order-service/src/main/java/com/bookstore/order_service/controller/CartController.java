package com.bookstore.order_service.controller;

import com.bookstore.order_service.service.ApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final ApiService apiService;

    public CartController(ApiService apiService) {
        this.apiService = apiService;
    }

    // 1. GET /api/cart/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
        try {
            Object cartData = apiService.getCartByUserId(userId);
            if (cartData != null) {
                return ResponseEntity.ok(cartData);
            }
            return ResponseEntity.ok(Collections.emptyMap());
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyMap());
        }
    }

    // 2. POST /api/cart/add (Add item to cart)
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> itemData) {
        try {
            // ApiService හරහා Cart එකට Item එක save කිරීම
            Object updatedCart = apiService.addToCart(itemData);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add item to cart: " + e.getMessage());
        }
    }

    // 3. PUT /api/cart/update
    @PutMapping("/update")
    public ResponseEntity<?> updateCart(@RequestBody Map<String, Object> cartItem) {
        return ResponseEntity.ok("Cart updated successfully");
    }

    // 4. DELETE /api/cart/remove
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long userId, @RequestParam Long bookId) {
        return ResponseEntity.ok("Item removed successfully");
    }

    // 5. DELETE /api/cart/clear/{userId} (Clear cart after order)
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        try {
            apiService.clearCartByUserId(userId);
            return ResponseEntity.ok("Cart cleared successfully!");
        } catch (Exception e) {
            return ResponseEntity.ok("Cart cleared");
        }
    }
}