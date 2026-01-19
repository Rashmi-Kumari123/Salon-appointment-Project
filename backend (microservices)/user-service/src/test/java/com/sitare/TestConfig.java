package com.sitare;

import com.sitare.payload.dto.UserDTO;
import net.devh.boot.grpc.server.config.GrpcServerProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public KafkaTemplate<String, UserDTO> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    public GrpcServerProperties grpcServerProperties() {
        return mock(GrpcServerProperties.class);
    }
}
