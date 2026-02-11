package com.microservices.user.dto;

import com.microservices.common.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'un utilisateur
 * 
 * @author Baye Rane
 * @version 1.0
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, max = 100, message = "Le mot de passe doit contenir entre 8 et 100 caractères")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre"
    )
    private String password;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String lastName;

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String phone;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String address;

    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    private String city;

    @Size(max = 100, message = "Le pays ne peut pas dépasser 100 caractères")
    private String country;

    @Size(max = 20, message = "Le code postal ne peut pas dépasser 20 caractères")
    private String postalCode;

    @Builder.Default
    private Role role = Role.USER;
}
