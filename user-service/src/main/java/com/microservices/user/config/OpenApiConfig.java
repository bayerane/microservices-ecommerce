package com.microservices.user.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration OpenAPI/Swagger pour User Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI userServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8082");
        devServer.setDescription("Serveur de développement");

        Server prodServer = new Server();
        prodServer.setUrl("https:/localhost:8080/api");
        prodServer.setDescription("Serveur de production (via Gateway)");

        Contact contact = new Contact();
        contact.setEmail("diopalassane89@gmail.com");
        contact.setName("Baye Rane");

        License license = new License()
            .name("MIT License")
            .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
            .title("User Service API")
            .version("1.0.0")
            .contact(contact)
            .description("API de gestion des utilisateurs. " +
                    "Ce service permet de créer, modifier, consulter et supprimer des profils utilisateurs. " +
                    "L'authentification est gérée par la Gateway via JWT.")
            .license(license);

        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer, prodServer))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtenu via /api/auth/login")
                )
            );
    }
}
