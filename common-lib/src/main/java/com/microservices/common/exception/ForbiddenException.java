package com.microservices.common.exception;

import com.microservices.common.enums.ErrorCode;

/**
 * Exception levée lorsqu'un utilisateur n'a pas les permissions nécessaires
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class ForbiddenException extends BusinessException {
    
    public ForbiddenException(String message) {
        super(ErrorCode.ACCESS_DENIED, message);
    }

    public ForbiddenException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }

    public static ForbiddenException insufficientPermissions() {
        return new ForbiddenException(ErrorCode.INSUFFICIENT_PERMISSIONS, "Vous n'avez pas les permissions nécessaires pour effectuer cette action.");
    }
    
    public static ForbiddenException adminOnly() {
        return new ForbiddenException(ErrorCode.INSUFFICIENT_PERMISSIONS, "Cette action est réservée aux administrateurs.");
    }

    public static ForbiddenException resourceAccess(String resource) {
        return new ForbiddenException(ErrorCode.ACCESS_DENIED,
            String.format("Vous n'êtes pas autorisé à accéder à cette ressource: %s", resource));
    }

    public static ForbiddenException operationNotAllowed(String operation) {
        return new ForbiddenException(ErrorCode.FORBIDDEN_OPERATION,
            String.format("L'opération '%s' n'est pas autorisée.", operation));
    }
}
