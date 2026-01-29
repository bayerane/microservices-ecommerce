package com.microservices.common.exception;

import com.microservices.common.enums.ErrorCode;

/**
 * Exception levée en cas de requête invalide
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class BadRequestException extends BusinessException {
    
    public BadRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }

    public BadRequestException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }

    public static BadRequestException invalidParameter(String paramName, String reason) {
        return new BadRequestException(ErrorCode.VALIDATION_ERROR,
            String.format("Paramètre invalide : %s. Raison : %s", paramName, reason)
        );
    }

    public static BadRequestException missingParameter(String paramName) {
        return new BadRequestException(ErrorCode.VALIDATION_ERROR,
            String.format("Paramètre requis manquant : %s", paramName));
    }

    public static BadRequestException invalidEmail(String email) {
        return new BadRequestException(ErrorCode.INVALID_EMAIL,
            String.format("Format d'email invalide : %s", email));
    }

    public static BadRequestException weakPassword() {
        return new BadRequestException(ErrorCode.WEAK_PASSWORD,
            "Le mot de passe doit contenir au moins 8 caractères, incluant majuscules, minuscules et chiffres.");
    }
    
    public static BadRequestException alreadyExists(String resource, String field, Object value) {
        return new BadRequestException(ErrorCode.DUPLICATE_ENTRY,
            String.format("%s avec %s '%s' existe déjà.", resource, field, value));
    }
}
