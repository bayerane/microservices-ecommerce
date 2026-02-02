package com.microservices.auth.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.microservices.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse de connexion
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    
    private String token;

    @Builder.Default
    private String type = "Bearer";

    private UUID userId;

    private String email;

    private Role role;

    private Long expiresIn;

    // Crée une LoginResponse à partir des données utilisateur
    public static LoginResponse of(String token, UUID userId, String email, Role role, Long expiresIn) {
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(userId)
                .email(email)
                .role(role)
                .expiresIn(expiresIn)
                .build();
    }
}
