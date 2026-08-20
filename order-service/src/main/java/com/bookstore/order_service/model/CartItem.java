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
    private String bookId; // 👈 book-catalog-service එකේ Book.id එක MongoDB String ID එකක් නිසා Long නෙවෙයි String වෙන්න ඕනේ
    private String bookTitle;
    private Integer quantity;
    private Double price;
}