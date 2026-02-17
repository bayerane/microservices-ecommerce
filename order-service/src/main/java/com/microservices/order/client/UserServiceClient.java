package com.microservices.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservices.common.dto.ApiResponse;
import com.microservices.order.dto.UserDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
 * Client Feign pour communiquer avec User Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@FeignClient(
    name = "user-service",
    path = "/users"
)
public interface UserServiceClient {
    
    // Récupère un utilisateur par son ID
    @GetMapping("/{userId}")
    @CircuitBreaker(name = "user-service", fallbackMethod = "getUserByIdFallback")
    ApiResponse<UserDTO> getUserById(@PathVariable String userId);

    // Fallback pour la récupèration d'utilisateur
    default ApiResponse<UserDTO> getUserByIdFallback(String userId, Exception ex) {
        UserDTO fallbackUser = new UserDTO();
        fallbackUser.setEmail("unknown@unknown.com");
        fallbackUser.setFullName("Utilisateur inconnu");
        return ApiResponse.success(fallbackUser, "Service utilisateur temporairement indisponible");
    }
}
