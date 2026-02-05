package com.microservices.gateway.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuration CORS pour la Gateway
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Configuration
public class CorsConfig {
    
    // Configure le filtre CORS pour permettre les requêtes cross-origin
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Origines autorisées (à adapter selon environnement)
        corsConfig.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",  // React dev
            "http://localhost:4200",  // Angular dev
            "http://localhost:8080",  // Gateway
            "http://localhost:*"      // Tous les ports localhost en dev
        ));

        // Méthodes HTTP autorisées
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS"
        ));

        // Headers autorisés
        corsConfig.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));

        // Headers exposés au client
        corsConfig.setExposedHeaders(Arrays.asList(
            "Authorization",
            "X-User-Id",
            "X-User-Email",
            "X-User-Role"
        ));

        // Autoriser l'envoi de credentials (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);

        // Durée de cache de la configuration CORS (1 heure)
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
