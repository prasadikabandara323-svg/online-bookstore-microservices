package com.example.BookStore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.BookStore.entity.User;

/**
 * Repository for User entity.
 * Extending MongoRepository gives us free CRUD methods automatically:
 * save(), findById(), findAll(), deleteById(), existsById(), count() ... etc.
 *
 * We only need to declare the CUSTOM queries below - Spring Data MongoDB
 * auto-generates the implementation just from the method name.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Used during LOGIN - find a user by their email
    Optional<User> findByEmail(String email);

    // Used during REGISTER - check if email is already taken before creating a new user
    boolean existsByEmail(String email);

}