package com.microservices.order.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplifié pour les données utilisateur (via Feign)
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String city;
    private String country;
}
