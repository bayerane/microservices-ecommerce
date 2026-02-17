package com.microservices.order.security;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.microservices.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilitaire pour gérer le contexte de sécurité Spring
 * Extrait les informations de l'utilisateur connecté depuis le JWT
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Component
@Slf4j
public class SecurityContextUtil {
    
    // Récupère l'ID de l'utilisateur connecté depuis le JWT
    public UUID getCurrentUserId() {
        Authentication authentication = getCurrentAuthentication();

        if (authentication == null || authentication.isAuthenticated()) {
            throw new BusinessException("Utilisateur non authentifiée");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String userIdStr = jwt.getClaimAsString("userId");

            if (userIdStr == null || userIdStr.isEmpty()) {
                log.error("JWT token manquant l'ID de l'utilisateur");
                throw new BusinessException("Token JWT invalide: userId manquant");
            }

            try {
                return UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                log.error("ID de l'utilisateur non valide: {}", userIdStr);
                throw new BusinessException("Format userId invalide dans le token");
            }
        }

        throw new BusinessException("Type de principal invalide");
    }

    // Récupère l'email de l'utilisateur connecté
    public String getCurrentUserEmail() {
        Authentication authentication = getCurrentAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("sub");
        }

        return null;
    }

    // Récupère les rôles de l'utilisateur connecté
    public Collection<? extends GrantedAuthority> getCurrentUserRoles() {
        Authentication authentication = getCurrentAuthentication();

        if (authentication != null) {
            return authentication.getAuthorities();
        }

        return null;
    }

    // Vérifie si l'utilisateur connecté est un administrateur
    public boolean isAdmin() {
        Collection<? extends GrantedAuthority> authorities = getCurrentUserRoles();

        if (authorities == null) {
            return false;
        }

        return authorities.stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Vérifie si l'utilisateur connecté est un utilisateur
    public boolean isUser() {
        Collection<? extends GrantedAuthority> authorities = getCurrentUserRoles();

        if (authorities == null) {
            return false;
        }

        return authorities.stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    // Vérifie que l'utilisateur est un administrateur
    // Lance une exception si ce n'est pas le cas
    public void requireAdmin() {
        if (!isAdmin()) {
            throw new BusinessException("Accès non autorisé");
        }
    }

    // Vérifie si l'utilisateur connecté a un ID spécifique
    public boolean isCurrentUser(UUID userId) {
        try {
            return getCurrentUserId().equals(userId);
        } catch (BusinessException e) {
            return false;
        }
    }

    // Vérifie si l'utilisateur peut accéder à une ressource
    // L'accès est autorisé si :
    // - L'utilisateur est ADMIN, OU
    // - L'utilisateur est le propriétaire (son ID correspond)
    public boolean canAccessResource(UUID resourceOwnerId) {
        return isAdmin() || isCurrentUser(resourceOwnerId);
    }

    // Vérifie l'accès à une ressource et lance une exception si refusé
    public void requireAccessToResource(UUID resourceOwnerId) {
        if (!canAccessResource(resourceOwnerId)) {
            throw new BusinessException("Accès non autorisé");
        }
    }

    // Récupère l'authentification courante
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // Récupère le JWT de l'utilisateur connecté
    public Jwt getCurrentJwt() {
        Authentication authentication = getCurrentAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        return null;
    }
}
