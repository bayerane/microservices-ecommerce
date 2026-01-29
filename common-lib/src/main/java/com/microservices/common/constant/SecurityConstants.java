package com.microservices.common.constant;

/**
 * Constantes liées à la sécurité
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class SecurityConstants {
    
    private SecurityConstants() {
        throw new UnsupportedOperationException("Classe de constantes, ne peut pas être instanciée.");
    }

    // JWT Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "Bearer";

    // Custom Headers (propagés par la Gateway)
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-User-Role";

    // JWT Claims
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_AUTHORITIES = "authorities";

    // JWT Configuration
    public static final long JWT_EXPIRATION = 86400000L; // 24 heures en millisecondes
    public static final long REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 jours
    public static final String JWT_ISSUER = "microservices-ecommerce";

    // Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PREFIX = "ROLE_";

    // Authorities
    public static final String AUTHORITY_USER = "ROLE_USER";
    public static final String AUTHORITY_ADMIN = "ROLE_ADMIN";

    // Public endpoints (ne nécessitent pas d'authentification)
    public static final String[] PUBLIC_ENDPOINTS = {
        "/api/auth/login",
        "/api/auth/register",
        "/actuator/**",
        "/swagger-ui/**",
        "/api-docs/**",
        "/v3/api-docs/**"
    };

    // Admin only endpoints
    public static final String[] ADMIN_ENDPOINTS = {
        "/api/users/admin/**",
        "/api/orders/admin/**"
    };

    // Password constraints
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    // BCrypt
    public static final int BCRYPT_STRENGTH = 10;

    // CORS
    public static final String[] ALLOWED_ORIGINS = {"http://localhost:3000", "http://localhost:4200"};
    public static final String[] ALLOWED_METHODS = {"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"};
    public static final String[] ALLOWED_HEADERS = {"*"};
    public static final long MAX_AGE = 3600L;

    // Rate Limiting
    public static final int MAX_REQUESTS_PER_MINUTE = 60;
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long LOGIN_ATTEMPT_LOCKOUT_DURATION = 900000L; // 15 minutes

    // Session
    public static final int SESSION_TIMEOUT_MINUTES = 30;

    // Security messages
    public static final String INVALID_CREDENTIALS_MESSAGE = "Email ou mot de passe incorrect";
    public static final String ACCOUNT_DISABLED_MESSAGE = "Votre compte est désactivé";
    public static final String ACCOUNT_LOCKED_MESSAGE = "Votre compte est verrouillé";
    public static final String TOKEN_EXPIRED_MESSAGE = "Votre session a expiré";
    public static final String ACCESS_DENIED_MESSAGE = "Accès refusé";
    public static final String INSUFFICIENT_PERMISSIONS_MESSAGE = "Permissions insuffisantes";
}
