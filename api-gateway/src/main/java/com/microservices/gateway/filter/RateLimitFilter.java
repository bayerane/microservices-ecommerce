package com.microservices.gateway.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtre global pour limiter les requêtes par minute
 * @author Baye Rane
 * @version 1.0
 */
@Component
@Slf4j
@ConditionalOnProperty(
    name = "spring.cloud.gateway.ratelimit.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {
    
    private final RedisRateLimiter redisRateLimiter;

    public RateLimitFilter(RedisRateLimiter redisRateLimiter) {
        super(Config.class);
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Récupèration de l'ID utilisateur injecté par le filtre JWT
            String userId = exchange.getRequest().getHeaders().getFirst("X-Uesr-Id");

            // Clé de limitation: ID utilisateur ou IP par défaut
            String key = (userId != null) ? userId :
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

            return redisRateLimiter.isAllowed(config.getRouteId(), key)
                .flatMap(response -> {
                    if (response.isAllowed()) {
                        return chain.filter(exchange);
                    } else {
                        log.warn("Rate limit dépassé pour l'utilisatgeur: {}", key);
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        return exchange.getResponse().setComplete();
                    }
                });
        };
    }

    @Data
    public static class Config {
        private String routeId;
    }
}
