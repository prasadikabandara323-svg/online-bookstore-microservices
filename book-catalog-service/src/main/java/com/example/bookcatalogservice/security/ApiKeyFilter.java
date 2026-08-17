package com.example.bookcatalogservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${app.api.key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();

        // 1. CORS Preflight Requests (OPTIONS) Bypass 
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Bypassing the filter only for static frontend assets (images, favicon) -
        // the root path ("/") now also requires the API key.
        if (path.equals("/index.html") || 
            path.startsWith("/bookcatelogimages/") || 
            path.equals("/favicon.ico")) {
            
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Retrieve the X-API-KEY from the request header.
        String requestApiKey = request.getHeader("X-API-KEY");

        // If the key is missing or the value does not match, the API request is rejected with a 401 error.
        if (requestApiKey == null || !requestApiKey.equals(expectedApiKey)) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized: Invalid or missing API Key\"}");
            return;
        }

        // If the key is correct, the API request is sent to the controller.
        filterChain.doFilter(request, response);
    }
}