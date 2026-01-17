package com.sitare.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * This filter is kept for backward compatibility but is no longer actively used.
 * User context information is now extracted in JwtAuthenticationFilter.
 * This filter can be removed if not needed elsewhere.
 */
@Component
public class UserContextFilter extends AbstractGatewayFilterFactory<UserContextFilter.Config> {

    public UserContextFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // User context is now handled by JwtAuthenticationFilter
            // This filter just passes through
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}
