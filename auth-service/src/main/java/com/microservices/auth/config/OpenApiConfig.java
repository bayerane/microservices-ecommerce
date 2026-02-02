package com.microservices.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration OpenAPI/Swagger pour Auth Service
 * 
 * @author Baye Rane 
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${spring.application.name")
    private String applicationName;

    @Bean
    public OpenAPI authServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("Serveur de développement");
       
        Server prodServer = new Server();
        prodServer.setUrl("http://localhost:8080/api");
        prodServer.setDescription("Serveur de production (via Gateway)");

        Contact contact = new Contact();
        contact.setEmail("diopalassane89@gmail.com");
        contact.setName("Baye Rane");

        License license = new License()
            .name("MIT License")
            .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
            .title("Auth Service API")
            .version("1.0.0")
            .contact(contact)
            .description("API d'authentification et de gestion des tokens JWT. " +
                    "Ce service gère l'authentification des utilisateurs, " +
                    "la génération de tokens JWT et l'enregistrement de nouveaux comptes.")
            .license(license);

        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer, prodServer));
    }
}
