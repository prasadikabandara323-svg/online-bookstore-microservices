package com.example.BookStore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Used ONLY for /auth/register-admin.
 * Same shape as RegisterRequest, but kept as a separate class on purpose -
 * it keeps the "public signup" and "admin creation" flows completely
 * independent, so a future change to one never accidentally affects the other.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}