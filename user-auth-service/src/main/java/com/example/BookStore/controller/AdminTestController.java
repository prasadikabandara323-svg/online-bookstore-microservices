package com.example.BookStore.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * EXAMPLE controller showing how to restrict an endpoint to ADMIN only.
 * @PreAuthorize checks the authorities set by JwtAuthenticationFilter
 * (the "ROLE_ADMIN" authority comes from the "role" claim inside the JWT).
 *
 * If a USER token calls this -> 403 Forbidden automatically, no extra code needed.
 * If no token at all -> 401 Unauthorized (blocked by SecurityConfig before even reaching here).
 */
@RestController
@RequestMapping("/admin")
public class AdminTestController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> adminOnly() {
        return ResponseEntity.ok(Map.of("message", "Welcome Admin! This endpoint is role-protected."));
    }
}