package com.microservices.common.enums;

/**
 * Énumération des rôles utilisateurs dans le système
 * 
 * @author Baye Rane
 * @version 1.0
 */
public enum Role {
    
    // Rôle utilisateur standard avec permissions limitées
    USER("USER", "Utilisateur standard"),
    // Rôle administrateur avec tous les privilèges
    ADMIN("ADMIN", "Administrateur du système");

    private final String code;
    private final String description;

    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // Récupère le code du rôle
    public String getCode() {
        return code;
    }

    // Récupère la description du rôle
    public String getDescription() {
        return description;
    }

    // Convertit une chaîne en enum Role
    public static Role fromCode(String code) {
        for (Role role : Role.values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Code de rôle invalide: " + code);
    }

    // Vérifie si le rôle est un administrateur
    public boolean isAdmin() {
        return this == ADMIN;
    }

    // Vérifie si le rôle est un utilisateur standard
    public boolean isUser() {
        return this == USER;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
