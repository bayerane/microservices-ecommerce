package com.microservices.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Feign Client
 * Propage le token JWT aux appels inter-services
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Configuration
@Slf4j
public class FeignConfig {
    
    // Intercepteur pour ajouter automatiquement le header Authorization
    // avec le token JWT de l'utilisateur connecté
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                    String token = jwt.getTokenValue();
                    template.header("Authorization", "Bearer " + token);
                    log.debug("JWT token propagated to Feign request");
                } else {
                    log.warn("No JWT token available for Feign request prpagation");
                }
            }
        };
    }
}
