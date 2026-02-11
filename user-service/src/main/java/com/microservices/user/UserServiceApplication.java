package com.microservices.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Point d'entrée du User Service
 * 
 * Service responsable de:
 * - La gestion complète des profils utilisateurs
 * - Le CRUD des utilisateurs
 * - La mise à jour des informations personnelles
 * - La gestion des mots de passe (via Auth Service)
 * 
 * Port: 8082
 * Swagger: http://localhost:8082/swagger-ui.html
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
		"com.microservices.user",
		"com.microservices.common"
})
@EnableDiscoveryClient
@EnableFeignClients
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
