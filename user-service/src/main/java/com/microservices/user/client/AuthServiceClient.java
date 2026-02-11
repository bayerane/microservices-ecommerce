package com.microservices.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.microservices.common.dto.ApiResponse;
import com.microservices.user.dto.PasswordUpdateRequest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
 * Client Feign pour communiquer avec Auth Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@FeignClient(
    name = "auth-service",
    path = "/auth"
)
public interface AuthServiceClient {

    // Vérifie les credentials d'un utilisateur
    @PostMapping("/verify-credentials")
    @CircuitBreaker(name = "auth-service", fallbackMethod = "verifyCredentialsFallback")
    ApiResponse<Boolean> verifyCredentials(
        @RequestParam String email,
        @RequestParam String password
    );

    // Met à jour le mot de passe d'un utilisateur
    @PutMapping("/users/{userId}/password")
    @CircuitBreaker(name = "auth-service", fallbackMethod = "updatePasswordFallback")
    ApiResponse<Void> updatePassword(
        @PathVariable String userId,
        @RequestBody PasswordUpdateRequest request
    );

    // Fallback pour la vérification des credentials
    default ApiResponse<Boolean> verifyCredentialsFallback(String email, String password, Exception ex) {
        return ApiResponse.error("Service d'authentification temporairement indisponible", false);
    }

    // Fallback pour la mise à jour du mot de passe
    default ApiResponse<Void> updatePasswordFallback(String userId, PasswordUpdateRequest request, Exception ex) {
        return ApiResponse.error("Service d'authentification temporairement indisponible");
    }
}
