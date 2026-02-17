package com.microservices.order.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration OpenAPI/Swagger pour Order Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8083}")
    private String serverPort;

    @Value("${spring.application.name:order-service}")
    private String applicationName;

    // Configuration OpenAPI principale
    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Order Service API")
                .description("API de gestion des commandes pour l'architecture microservices.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Baye Rane")
                    .email("diopalassane89@gmail.com")
                    .url("https://github.com/bayerane/microservices-ecommerce.git"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://github.com/bayerane/microservices-ecommerce/blob/main/LICENSE")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Environnement de développement local"),
                new Server()
                    .url("https://localhost:8080/orders")
                    .description("Via API Gateway")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("""
                            Entrez le token JWT Bearer obtenu depuis le Auth Service.
                            Format: Bearer <token>
                        """)
                )
            );
    }
}
