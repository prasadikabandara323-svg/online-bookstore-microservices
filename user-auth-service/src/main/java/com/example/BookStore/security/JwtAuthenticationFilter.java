package com.example.BookStore.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Runs ONCE per incoming HTTP request, BEFORE it reaches any controller.
 *
 * What it does:
 *  1. Looks for "Authorization: Bearer <token>" header
 *  2. If present and valid -> tells Spring Security "this request is from
 *     an authenticated user with this role" (sets the SecurityContext)
 *  3. If missing or invalid -> just lets the request continue with NO authentication
 *     (SecurityConfig then decides whether that endpoint requires auth or not)
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private static final String HEADER_NAME = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HEADER_NAME);

        // No token provided -> just continue, let SecurityConfig decide if this endpoint needs auth
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            final String email = jwtUtil.extractEmail(token);

            // Only set authentication if not already set for this request
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtUtil.isTokenValid(token, email)) {

                    String role = jwtUtil.extractRole(token); // "USER" or "ADMIN"

                    // Spring Security expects roles prefixed with "ROLE_" for hasRole() to work
                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // This is what marks the request as "authenticated" for the rest of the pipeline
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            // Invalid/expired/tampered token -> leave request unauthenticated
            // (don't throw here - let Spring Security's entry point return 401/403 naturally)
        }

        filterChain.doFilter(request, response);
    }
}