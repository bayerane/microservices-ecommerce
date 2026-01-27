package com.microservices.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false"
})
@ActiveProfiles("test")
class DiscoveryServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
