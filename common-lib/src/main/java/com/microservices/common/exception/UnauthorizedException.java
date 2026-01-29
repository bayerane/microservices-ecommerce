package com.microservices.common.exception;

import com.microservices.common.enums.ErrorCode;

/**
 * Exception levée en cas d'échec d'authentification
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class UnauthorizedException extends BusinessException {
    
    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED_ACCESS, message);
    }

    public UnauthorizedException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }

    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS, "Email ou mot de passe incorrect.");
    }

    public static UnauthorizedException tokenExpired() {
        return new UnauthorizedException(ErrorCode.TOKEN_EXPIRED, "Votre session a expiré, veuillez vous reconnecter.");
    }

    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException(ErrorCode.TOKEN_INVALID, "Token invalide ou malformé.");
    }

    public static UnauthorizedException missingToken() {
        return new UnauthorizedException(ErrorCode.UNAUTHORIZED_ACCESS, "Token d'authentification manquant.");
    }
    
}
