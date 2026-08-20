package com.example.BookStore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the Swagger UI (Docker container, port 9090) to call this service
 * directly for testing/documentation purposes.
 *
 * IMPORTANT:
 * 1) Do NOT add the client-app origin (localhost:3000) here. The real
 *    client app must always go through the API Gateway, which has its own
 *    separate CORS policy. Adding it here too would make the browser see
 *    TWO Access-Control-Allow-Origin values on the final response.
 * 2) Only map Swagger's own paths ("/swagger-ui/**", "/v3/api-docs/**") -
 *    NOT "/**". Requests forwarded by the Gateway still carry the
 *    browser's original Origin header (e.g. http://127.0.0.1:3000). If the
 *    mapping covers "/**", Spring MVC's CORS check runs on those forwarded
 *    requests too and REJECTS them with 403 "Invalid CORS request" (since
 *    that origin isn't in this service's allowed list) - even though this
 *    hop is server-to-server (Gateway -> this service), not a real
 *    browser request. Scoping the mapping to Swagger's paths keeps that
 *    check from touching the "/auth/**" API routes at all.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("http://localhost:9091", "http://127.0.0.1:9091")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("http://localhost:9091", "http://127.0.0.1:9091")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}