package com.sitare.authservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for gRPC clients
 * TODO: Implement gRPC client configuration if needed for inter-service communication
 */
@Configuration
public class GrpcClientConfig {

    private static final Logger log = LoggerFactory.getLogger(GrpcClientConfig.class);

    // TODO: Add gRPC client beans if needed
    // For now, auth-service doesn't need to call other services via gRPC
}
