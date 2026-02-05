package com.microservices.gateway.filter;

import com.microservices.gateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour JwtAuthenticationFilter
 * 
 * @author Baye Rane
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        filter.jwtUtil = jwtUtil;
    }

    @Test
    void testFilterWithValidToken() {
        // Arrange
        String validToken = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
            .build();
        
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUserId(validToken)).thenReturn("user-id-123");
        when(jwtUtil.extractEmail(validToken)).thenReturn("user@test.com");
        when(jwtUtil.extractRole(validToken)).thenReturn("USER");
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = filter.apply(new JwtAuthenticationFilter.Config())
            .filter(exchange, chain);

        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).extractUserId(validToken);
        verify(jwtUtil).extractEmail(validToken);
        verify(jwtUtil).extractRole(validToken);
        verify(chain).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilterWithMissingAuthorizationHeader() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .build();
        
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        Mono<Void> result = filter.apply(new JwtAuthenticationFilter.Config())
            .filter(exchange, chain);

        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilterWithInvalidTokenFormat() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .header(HttpHeaders.AUTHORIZATION, "InvalidFormat token")
            .build();
        
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        Mono<Void> result = filter.apply(new JwtAuthenticationFilter.Config())
            .filter(exchange, chain);

        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilterWithExpiredToken() {
        // Arrange
        String expiredToken = "expired.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
            .build();
        
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        when(jwtUtil.validateToken(expiredToken)).thenReturn(false);

        // Act
        Mono<Void> result = filter.apply(new JwtAuthenticationFilter.Config())
            .filter(exchange, chain);

        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(jwtUtil).validateToken(expiredToken);
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }
}