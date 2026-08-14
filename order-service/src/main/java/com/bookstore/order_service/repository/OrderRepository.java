package com.bookstore.order_service.repository;

import com.bookstore.order_service.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    // User ID එක අනුව Orders Fetch කරගැනීමට (Frontend එකේ My Orders වලට අවශ්‍ය වේ නම්)
    List<Order> findByUserId(Long userId);
}