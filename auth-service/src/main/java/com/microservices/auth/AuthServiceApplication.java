package com.microservices.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Point d'entrée du Auth Service
 * 
 * Service responsable de:
 * - L'authentification des utilisateurs
 * - La génération de tokens JWT
 * - L'enregistrement de nouveaux utilisateurs
 * - La validation des tokens
 * 
 * Port: 8081
 * Swagger: http://localhost:8081/swagger-ui.html
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
	"com.microservices.auth",
	"com.microservices.common"
})
@EnableDiscoveryClient
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
