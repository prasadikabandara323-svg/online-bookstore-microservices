package com.example.ApiGateway.security;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Simple in-memory rate limiter - no Redis needed (keeps the project
 * simpler for a student assignment; Spring Cloud Gateway's built-in
 * RequestRateLimiter filter requires Redis).
 *
 * Strategy: FIXED WINDOW per client IP.
 *  - Each IP gets a counter + a "window start" timestamp.
 *  - Counter resets to 0 every time a new 60-second window starts.
 *  - If an IP exceeds MAX_REQUESTS_PER_WINDOW within that window -> 429.
 *
 * Limitation (worth mentioning in the report): this is per-instance memory,
 * so it resets if the Gateway restarts, and wouldn't be shared across
 * multiple Gateway instances in a real production deployment (that's
 * exactly why real systems use Redis - a shared store all instances read/write).
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final int MAX_REQUESTS_PER_WINDOW = 20; // requests
    private static final long WINDOW_SIZE_MS = 60_000;      // per 60 seconds

    private final ConcurrentHashMap<String, ClientRequestWindow> requestCounts = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = getClientIp(exchange.getRequest());

        ClientRequestWindow window = requestCounts.computeIfAbsent(clientIp, k -> new ClientRequestWindow());

        long now = System.currentTimeMillis();

        synchronized (window) {
            if (now - window.windowStart > WINDOW_SIZE_MS) {
                // window expired - start a fresh one
                window.windowStart = now;
                window.count.set(0);
            }

            int currentCount = window.count.incrementAndGet();

            if (currentCount > MAX_REQUESTS_PER_WINDOW) {
                return rejectRequest(exchange);
            }
        }

        return chain.filter(exchange);
    }

    private String getClientIp(ServerHttpRequest request) {
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json");

        String body = String.format(
                "{\"status\":429,\"message\":\"Too many requests. Limit is %d requests per minute.\"}",
                MAX_REQUESTS_PER_WINDOW
        );
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -2; // run BEFORE the JWT filter - reject early, don't waste effort validating tokens for blocked clients
    }

    /** Tracks one client IP's request count within the current time window. */
    private static class ClientRequestWindow {
        volatile long windowStart = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
    }
}