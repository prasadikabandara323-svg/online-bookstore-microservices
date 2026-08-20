package com.example.BookStore.config;

import java.time.LocalDateTime; // Role එක import කරගැනීම
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.BookStore.entity.Role;
import com.example.BookStore.entity.User;
import com.example.BookStore.repository.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setFullName("prasadika");
                admin.setEmail("prasadikabandara323@gmail.com");
                admin.setPassword(passwordEncoder.encode("prasa@2000"));
                admin.setRole(Role.ADMIN); // String වෙනුවට Role Enum එක භාවිතා කිරීම
                admin.setEnabled(true);
                admin.setCreatedAt(LocalDateTime.now());

                User user = new User();
                user.setFullName("Ayodhya");
                user.setEmail("ayodhyaranathunge@gmail.com");
                user.setPassword(passwordEncoder.encode("123456"));
                user.setRole(Role.USER); // String වෙනුවට Role Enum එක භාවිතා කිරීම
                user.setEnabled(true);
                user.setCreatedAt(LocalDateTime.now());

                userRepository.saveAll(List.of(admin, user));
                System.out.println("✅ User Auth Service Data Seeded Successfully!");
            }
        };
    }
}