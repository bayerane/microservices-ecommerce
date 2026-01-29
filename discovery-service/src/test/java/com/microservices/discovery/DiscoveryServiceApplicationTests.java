package com.microservices.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration pour le Discovery Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
	"spring.cloud.discovery.enabled=true",
	"spring.main.allow-bean-definition-overriding=true"
})
class DiscoveryServiceApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
	
	// Vérifie que le contexte Spring se charge correctement
    @Test
    void contextLoads() {
        // Le test passe si le contexte se charge sans erreur
    }

    // Vérifie que le dashboard Eureka est accessible
    @Test
    void eurekaHomepageIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/",
            String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Eureka");
    }

    // Vérifie que l'endpoint /eureka/apps est accessible
    @Test
    void eurekaAppsEndpointIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/eureka/apps",
            String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // Vérifie que l'actuator health est accessible
    @Test
    void actuatorHealthEndpointIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/actuator/health",
            String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }

    // Vérifie que l'actuator info est accessible
    @Test
    void actuatorInfoEndpointIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/actuator/info",
            String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Discovery Service");
    }
}