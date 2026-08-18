package com.example.BookStore.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String fullName;

    @Indexed(unique = true)
    private String email;

    private String password;

    // Base64 data-URI of the profile picture (e.g. "data:image/png;base64,...").
    // Stored directly on the document - simplest option, no file server needed.
    private String profileImage;

    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private boolean enabled = true;

    private LocalDateTime createdAt;

    // Password reset OTP fields
    private String otpCode;
    private LocalDateTime otpExpiry;

    public void prePersistDefaults() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}