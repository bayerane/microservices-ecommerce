package com.microservices.discovery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité pour le Discovery Service
 * 
 * Désactive la protection CSRF car Eureka utilise des endpoints REST
 * et permet l'accès au dashboard sans authentification en développement.
 * 
 * En production, il est recommandé d'ajouter une authentification basique.
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactive CSRF car Eureka est un service REST
            .csrf(csrf -> csrf.disable())
            
            // Autorise toutes les requêtes (pas d'authentification en dev)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}

/*
 * CONFIGURATION POUR LA PRODUCTION (avec authentification) :
 * 
 * Pour sécuriser Eureka en production, décommenter et utiliser :
 * 
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig {
 * 
 *     @Bean
 *     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *         http
 *             .csrf(csrf -> csrf.disable())
 *             .authorizeHttpRequests(auth -> auth
 *                 .anyRequest().authenticated()
 *             )
 *             .httpBasic(Customizer.withDefaults());
 *         
 *         return http.build();
 *     }
 * 
 *     @Bean
 *     public UserDetailsService userDetailsService() {
 *         UserDetails admin = User.builder()
 *             .username("admin")
 *             .password("{noop}admin123") // Utiliser BCrypt en production
 *             .roles("ADMIN")
 *             .build();
 *         
 *         return new InMemoryUserDetailsManager(admin);
 *     }
 * }
 * 
 * Puis mettre à jour application.yml :
 * 
 * eureka:
 *   client:
 *     service-url:
 *       defaultZone: http://admin:admin123@localhost:8761/eureka/
 */