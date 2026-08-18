package com.example.BookStore.dto;

import com.example.BookStore.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String id;
    private String fullName;
    private String email;
    private String profileImage;
    private Role role;
    private String message;
}