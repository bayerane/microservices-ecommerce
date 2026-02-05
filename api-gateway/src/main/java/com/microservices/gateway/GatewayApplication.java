package com.microservices.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Point d'entrée de l'API Gateway
 * 
 * Responsabilités:
 * - Point d'entrée unique pour tous les micro-services
 * - Routage des requêtes vers les services appropriés
 * - Validation JWT et enrichissement des requêtes
 * - Load balancing via Eureka
 * - Gestion CORS
 * - Logging centralisé
 * 
 * Port: 8080
 * 
 * Routes configurées:
 * - /api/auth/** -> auth-service
 * - /api/users/** -> user-service
 * - /api/orders/** -> order-service
 * - /eureka/** -> discovery-service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
	"com.microservices.gateway",
	"com.microservices.common"
})
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
