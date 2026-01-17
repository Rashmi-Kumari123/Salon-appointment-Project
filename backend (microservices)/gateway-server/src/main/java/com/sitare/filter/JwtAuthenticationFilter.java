package com.sitare.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret:your-256-bit-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long}")
    private String jwtSecret;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public String name() {
        return "JwtAuthentication";
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Skip authentication for public endpoints
            String path = request.getURI().getPath();
            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            // Get token from Authorization header
            String authHeader = request.getHeaders().getFirst("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // Validate token
                if (!validateToken(token)) {
                    return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                }

                // Extract claims
                Claims claims = extractClaims(token);
                String role = claims.get("role", String.class);
                
                // Check role-based authorization for specific paths
                if (requiresSalonOwnerRole(path) && !isSalonOwnerOrAdmin(role)) {
                    return onError(exchange, "Access denied. Salon owner or admin role required.", HttpStatus.FORBIDDEN);
                }
                
                // Add user info to request headers for downstream services
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Email", claims.getSubject())
                        .header("X-User-Role", role)
                        .header("X-User-Id", String.valueOf(claims.get("userId", Long.class)))
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/auth/") || 
               path.startsWith("/api/notifications/ws/") ||
               path.equals("/actuator/health");
    }

    private boolean requiresSalonOwnerRole(String path) {
        return path.contains("/salon-owner/") ||
               path.contains("/api/categories/salon-owner/") ||
               path.contains("/api/notifications/salon-owner/") ||
               path.contains("/api/service-offering/salon-owner/");
    }

    private boolean isSalonOwnerOrAdmin(String role) {
        return "SALON_OWNER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    private boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        return response.writeWith(Mono.just(response.bufferFactory().wrap(
                ("{\"error\":\"" + message + "\"}").getBytes()
        )));
    }

    public static class Config {
        // Configuration properties if needed
    }
}
