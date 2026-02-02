package com.microservices.auth.dto;

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
 * DTO pour la requête d'enregistrement
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    
    @NotBlank(message= "L'email est obligatoire")
    @Email(message= "Format d'email invalide")
    @Size(max= 255, message= "L'email ne peut pas dépasser 255 caractères")
    private String email;

    @NotBlank(message= "Le mot de passe est obligatoire")
    @Size(min= 8, max= 100, message= "Le mot de passe doit contenir entre 8 et 100 caractères")
    @Pattern(
        regexp= "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message= "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre"
    )
    private String password;

    @NotBlank(message= "La confirmation du mot de passe est obligatoire")
    private String confirmPassword;

    @Builder.Default
    private Role role = Role.USER;

    // Vérifie si les mots de passe correspondent
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
