package com.microservices.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservices.gateway.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration des routes de la Gateway
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RouteConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Configure les routes de la Gateway
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuration Gateway routes");

        return builder.routes()

        // ========================================
        // Route vers Auth Service (endpoints publics)
        // ========================================
        .route("auth-service-public", r -> r
            .path("/api/auth/login", "/api/auth/register", "/api/auth/health")
            .filters(f -> f
                .stripPrefix(1) // Retire /api du path
                .retry(retryConfig -> retryConfig
                    .setRetries(3)
                    .setBackoff(java.time.Duration.ofMillis(100), java.time.Duration.ofMillis(1000), 2, true)
                )
            )
            .uri("lb://auth-service")
        )

        // ========================================
        // Route vers Auth Service (endpoints protégés)
        // ========================================
        .route("auth-service-protected", r -> r
            .path("/api/auth/**")
            .filters(f -> f
                .stripPrefix(1) // Retire /api du path
                .filter(jwtAuthenticationFilter
                    .apply(new JwtAuthenticationFilter.Config())
                )
            )
            .uri("lb://auth-service")
        )

        // ========================================
        // Route vers User Service
        // ========================================
        .route("user-service", r -> r
            .path("/api/users/**")
            .filters(f -> f
                .stripPrefix(1) // Retire /api du path
                .filter(jwtAuthenticationFilter
                    .apply(new JwtAuthenticationFilter.Config())
                )
                .retry(retryConfig -> retryConfig
                    .setRetries(3)
                    .setBackoff(java.time.Duration.ofMillis(100), java.time.Duration.ofMillis(1000), 2, true)
                )
            )
            .uri("lb://user-service")
        )

        // ========================================
        // Route vers Order Service
        // ========================================
        .route("order-service", r -> r
            .path("/api/orders/**")
            .filters(f -> f
                .stripPrefix(1) // Retire /api du path
                .filter(jwtAuthenticationFilter
                    .apply(new JwtAuthenticationFilter.Config())
                )
                .retry(retryConfig -> retryConfig
                    .setRetries(3)
                    .setBackoff(java.time.Duration.ofMillis(100), java.time.Duration.ofMillis(1000), 2, true)
                )
            )
            .uri("lb://order-service")
        )

        // ========================================
        // Route vers Discovery Service (Eureka Dashboard)
        // ========================================
        .route("discovery-service", r -> r
            .path("/eureka/**")
            .uri("lb://discovery-service")
        )

        .build();
    }
}
