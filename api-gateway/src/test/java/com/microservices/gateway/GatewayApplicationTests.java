package com.microservices.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests de démarrage de l'application Gateway
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
class GatewayApplicationTests {

    /**
     * Vérifie que le contexte Spring se charge correctement
     */
    @Test
    void contextLoads() {
        // Le test passe si le contexte se charge sans erreur
    }
}