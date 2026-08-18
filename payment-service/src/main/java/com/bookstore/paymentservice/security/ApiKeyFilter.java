package com.bookstore.paymentservice.security; // ඔයාගේ package name එකට ගැලපෙන සේ වෙනස් කරන්න

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

    @Value("${api.security.key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Browser එකෙන් එන CORS Preflight (OPTIONS) requests Bypass කිරීම
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Static HTML, CSS, JS, Images සහ Root URL (/) සඳහා API Key Security Bypass කිරීම
        String path = request.getRequestURI();
        if (path.equals("/") || 
            path.endsWith(".html") || 
            path.endsWith(".css") || 
            path.endsWith(".js") || 
            path.endsWith(".png") || 
            path.endsWith(".jpg") || 
            path.endsWith(".ico")) {
            
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Header එකෙන් X-API-KEY එක ලබා ගැනීම (API Endpoints සඳහා පමණයි)
        String apiKeyHeader = request.getHeader("X-API-KEY");

        // Key එක නිවැරදිදැයි පරීක්ෂා කිරීම
        if (expectedApiKey.equals(apiKeyHeader)) {
            filterChain.doFilter(request, response); // Key එක හරි නම් request එක ඉදිරියට යවනවා
        } else {
            // Key එක වැරදි නම් 401 Unauthorized Error එකක් යවනවා
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or Missing API Key");
        }
    }
}