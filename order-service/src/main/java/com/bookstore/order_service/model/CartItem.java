package com.bookstore.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    private String id;

    private Long userId;
    private Long bookId;
    private String bookTitle; // 👈 මෙන්න මේ Field එක එකතු කරන්න
    private Integer quantity;
    private Double price;
}