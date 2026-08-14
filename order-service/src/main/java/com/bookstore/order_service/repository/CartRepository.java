package com.bookstore.order_service.repository;

import com.bookstore.order_service.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findByUserId(Long userId);
}