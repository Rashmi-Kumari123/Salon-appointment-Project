package com.sitare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/api/notifications/ws/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().permitAll() // JWT filter handles authentication via route configuration
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return serverHttpSecurity.build();
    }
}