package com.microservices.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de sécurité pour la Gateway
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    /**
     * Configure la chaîne de filtres de sécurité
     * 
     * La Gateway délègue l'authentification aux filtres personnalisés (JWT)
     * Spring Security est configuré en mode permissif
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            // Désqctive CSRF (car utilisation de JWT stateless)
            .csrf(csfr -> csfr.disable())
            // Désactive la protection CORS (gèrée par CorsWebFilter)
            .cors(cors -> cors.disable())
            // Configuration des autorisations
            .authorizeExchange(exchange -> exchange
                // Endpoints publics (pas d'authentification requise)
                .pathMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/health",
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/**",
                    "/eureka/**"
                ).permitAll()
                // Tous les autres endpoints doivent avoir une authentification
                // (gérée par JwtAuthenticationFilter)
                .anyExchange().permitAll()
            );

        return http.build();
    }
}
