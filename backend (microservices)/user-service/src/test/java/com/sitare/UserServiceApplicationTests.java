package com.sitare;

import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {GrpcServerAutoConfiguration.class})
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
