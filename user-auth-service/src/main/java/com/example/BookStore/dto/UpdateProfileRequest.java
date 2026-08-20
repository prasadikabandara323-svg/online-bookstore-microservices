package com.example.BookStore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Both fields optional - a request can update just the name, just the
 * picture, or both. null means "leave this field unchanged".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String profileImage; // base64 data-URI, e.g. "data:image/png;base64,..."
}