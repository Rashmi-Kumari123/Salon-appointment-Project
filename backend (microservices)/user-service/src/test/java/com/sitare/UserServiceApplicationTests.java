package com.sitare;

import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    properties = {
        "eureka.client.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration,net.devh.boot.grpc.server.autoconfigure.GrpcMetadataEurekaConfiguration"
    }
)
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {GrpcServerAutoConfiguration.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = net.devh.boot.grpc.server.service.GrpcService.class))
@Import(TestConfig.class)
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
