package com.microservices.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Filtre global pour logger toutes les requêtes et réponses
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Log de la requête entrant
        log.info(
            "Incoming Request: {} {} from {}",
            request.getMethod(),
            request.getURI().getPath(),
            request.getRemoteAddress()
        );

        // Log des headers principaux (sans le token complet pour sécurité)
        if (request.getHeaders().containsKey("Authorization")) {
            log.debug("Authorization header present");
        }

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;

            // Log de la réponse
            log.info(
                "Outgoing Response: {} {} - Status: {} - Duration: {}ms",
                request.getMethod(),
                request.getURI().getPath(),
                response.getStatusCode(),
                duration
            );

            // Log warning si la requête est lente
            if (duration > 1000) {
                log.warn(
                    "Slow request detected: {} {} took {}ms",
                    request.getMethod(),
                    request.getURI().getPath(),
                    duration
                );
            }
        }));
    }

    @Override
    public int getOrder() {
        // S'exécute en premier (avant les autres filtres)
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
