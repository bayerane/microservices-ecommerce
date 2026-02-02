package com.microservices.auth.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.microservices.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO générique pour les réponses d'authentification
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    
    private UUID userId;
    private String email;
    private Role role;
    private Boolean enabled;
    private String message;

    // Crée une AuthResponse pour un utilisateur enregistré
    public static AuthResponse registered(UUID userId, String email, Role role) {
        return AuthResponse.builder()
                .userId(userId)
                .email(email)
                .role(role)
                .enabled(true)
                .message("Utilisateur enregistré avec succès.")
                .build();
    }
}
