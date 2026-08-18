package com.example.BookStore.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Value("${api.key}")
    private String validApiKey;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/swagger-ui", "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // CORS preflight requests never carry custom headers (like X-API-KEY) -
        // that's the browser's design, not something we can change. Let them
        // through untouched so the browser's actual CORS check can proceed.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        boolean isExcluded = EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
        if (isExcluded) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedKey = request.getHeader(API_KEY_HEADER);

        if (providedKey == null || !providedKey.equals(validApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":401,\"message\":\"Missing or invalid API Key. " +
                    "This endpoint must be accessed through the API Gateway.\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}