package com.bookstore.order_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${app.api.key:YOUR_SECRET_API_KEY_HERE}")
    private String configuredApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Browser Preflight (OPTIONS) requests වලදී API key check නොකර passing දෙන්න
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        String requestPath = request.getRequestURI();

// Swagger & OpenAPI endpoints වලට API Key රහිතව access දෙන්න
if (requestPath.contains("/swagger-ui") || 
    requestPath.contains("/v3/api-docs") || 
    requestPath.contains("/openapi.yaml") || 
    requestPath.contains("/swagger-resources") || 
    requestPath.contains("/webjars")) {
    
    filterChain.doFilter(request, response);
    return;
    }

        // 3. Header එකෙන් X-API-KEY එක ගන්න
        String apiKeyHeader = request.getHeader("X-API-KEY");

        // Null safe validation check
        if (apiKeyHeader != null && apiKeyHeader.trim().equals(configuredApiKey.trim())) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken("API_USER", null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            // API key එක නැත්නම් හරි වැරදි නම් හරි 401 Unauthorized යවන්න
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized: Invalid or Missing API Key\"}");
        }
    }
}