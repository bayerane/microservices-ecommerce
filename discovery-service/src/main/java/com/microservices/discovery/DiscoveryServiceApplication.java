package com.microservices.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Point d'entrée du Discovery Service (Eureka Server)
 * 
 * Ce service permet la découverte automatique de tous les micro-services
 * de l'architecture. Les services clients s'enregistrent automatiquement
 * au démarrage et peuvent ensuite se découvrir mutuellement.
 * 
 * Dashboard accessible sur : http://localhost:8761
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServiceApplication.class, args);
	}

}
