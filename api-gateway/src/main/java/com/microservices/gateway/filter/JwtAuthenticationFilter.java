package com.microservices.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.microservices.gateway.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Filtre Gateway pour l'authentification JWT
 * 
 * Responsabilités :
 * - Extraire et valider le token JWT
 * - Enrichir la requête avec les headers X-User-Id, X-User-Email, X-User-Role
 * - Bloquer les requêtes avec token invalide
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    @Autowired
    public JwtUtil jwtUtil;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest();

            log.debug("Processing request: {} {}", request.getMethod(), request.getURI().getPath());

            // Vérifier la présence du header Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Missing Authorization header for path: {}", request.getURI().getPath());
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            // Extraire le token
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                log.warn("Invalid Authorization header for path: {}", request.getURI().getPath());
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }

            // Valider le token
            try {
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Invalid or expired token for path: {}", request.getURI().getPath());
                    return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                }

                // Extraire les informations du token
                String userId = jwtUtil.extractUserId(token);
                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);

                log.debug("Authenticated user: {} ({}), role: {}", email, userId, role);

                // Enrichir la requête avec les headers personnalisés
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();

                ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();

                log.debug("Request enriched with context headers");

                return chain.filter(modifiedExchange);
            } catch (Exception e) {
                log.error("Error processing JWT token: {}", e.getMessage(), e);
                return onError(exchange, "Error processing authentication", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    // Gère les erreurs en retournant une réponse appropriée
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        log.error("Authentication error: {} - Status: {}", message, httpStatus);

        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String errorResponse = String.format(
            "{\"success\":false,\"message\":\"%s\",\"status\":%d,\"timestamp\":\"%s\"}",
            message,
            httpStatus.value(),
            java.time.LocalDateTime.now()
        );

        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes()))
        );
    }

    // Classe de configuration du filter
    public static class Config {
        // Permet d'activer ou désactiver le filtre pour une route spécifique
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
