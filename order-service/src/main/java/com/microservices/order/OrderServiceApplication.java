package com.microservices.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Classe principale du Order Service
 * Gère les commandes dans l'architecture microservices
 * 
 * Fonctionnalités :
 * - CRUD complet des commandes
 * - Gestion des statuts avec transitions validées
 * - Permissions basées sur les rôles (USER/ADMIN)
 * - Communication avec User Service via Feign
 * - Génération de numéros de commande uniques
 * - Recherches et statistiques avancées
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.microservices.order",
    "com.microservices.common"
})
@EnableFeignClients
@EnableJpaAuditing
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
