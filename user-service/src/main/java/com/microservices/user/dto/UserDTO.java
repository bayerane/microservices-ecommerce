package com.microservices.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les données utilisateur
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Crée un UserDTO avec nom complet
    public static UserDTO minimal(UUID id, String email, String firstName, String lastName) {
        return UserDTO.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(firstName + " " + lastName)
                .build();
    }
}
