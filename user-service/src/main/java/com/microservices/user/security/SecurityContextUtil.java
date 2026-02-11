package com.microservices.user.security;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.microservices.common.enums.Role;
import com.microservices.common.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilitaire pour récupérer le contexte utilisateur depuis les headers
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Component
@Slf4j
public class SecurityContextUtil {
    
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    // Récupère la requête HTTP courante
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new UnauthorizedException("Aucun contexte de requête disponible");
        }

        return attributes.getRequest();
    }

    // Récupère l'ID de l'utilisateur courant depuis les headers
    public UUID getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        String userId =  request.getHeader(USER_ID_HEADER);

        if (userId == null || userId.isEmpty()) {
            log.error("Header X-User-Id manquant dans la requête");
            throw new UnauthorizedException("Contexte utilisateur manquant");
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.error("Format UUID invalide pour X-User-Id: {}", userId);
            throw new UnauthorizedException("Contexte utilisateur invalide");
        }
    }

    // Récupère l'email de l'utilisateur courant
    public String getCurrentUserEmail() {
        HttpServletRequest request = getCurrentRequest();
        String email = request.getHeader(USER_EMAIL_HEADER);

        if (email == null || email.isEmpty()) {
            log.error("Header X-User-Email manquant dans la requête");
            throw new UnauthorizedException("Contexte utilisateur manquant");
        }

        return email;
    }

    // Récupère le rôle de l'utilisateur courant
    public Role getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        String role = request.getHeader(USER_ROLE_HEADER);

        if (role == null || role.isEmpty()) {
            log.error("Header X-User-Role manquant dans la requête");
            throw new UnauthorizedException("Contexte utilisateur manquant");
        }

        try {
            return Role.fromCode(role);
        } catch (IllegalArgumentException e) {
            log.error("Rôle invalide: {}", role);
            throw new UnauthorizedException("Contexte utilisateur invalide");
        }
    }

    // Vérifie si l'utilisateur courant est admin
    public boolean isCurrentUserAdmin() {
        return getCurrentUserRole().isAdmin();
    }

    // Vérifie si l'utilisateur courant a l'ID spécifié
    public boolean isCurrentUser(UUID userId) {
        return getCurrentUserId().equals(userId);
    }

    // Vérifie si l'utilisateur peut accéder à la ressource
    // (soit c'est son profil, soit c'est admin)
    public boolean canAccessResource(UUID userId) {
        return isCurrentUser(userId) || isCurrentUserAdmin();
    }

    // Vérifie et lève une exception si l'utilisateur ne peut pas accéder
    public void requireAccessToResource(UUID userId) {
        if (!canAccessResource(userId)) {
            log.warn("Accès refusé: utilisateur {} tente d'accéder à la ressource {}", getCurrentUserId(), userId);
            throw new UnauthorizedException("Vous n'êtes pas autorisé à accèder à cette ressource");
        }
    }
}
