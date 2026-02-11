package com.microservices.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité pour User Service
 * 
 * La sécurité est gérée par la Gateway (JWT validation)
 * Ce service lit simplement les headers X-User-* ajoutés par la Gateway
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    // Configure la chaîne de filtres de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactive CSRF (stateless avec JWT)
            .csrf(AbstractHttpConfigurer::disable)

            // Configuration des autorisations
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics (monitoring)
                .requestMatchers(
                    "/actuator/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api-docs/**"
                ).permitAll()

                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().permitAll()
            )

            // Gestion de session stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
