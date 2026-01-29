package com.microservices.common.exception;

import com.microservices.common.enums.ErrorCode;

/**
 * Exception levée en cas d'erreur interne du serveur
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class InternalServerException extends BusinessException {
    
    public InternalServerException(String message) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, message, cause);
    }

    public InternalServerException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }

    public static InternalServerException databaseError(String operation) {
        return new InternalServerException(ErrorCode.DATABASE_ERROR,
            String.format("Erreur lors de l'opération de base de données : %s.", operation));
    }

    public static InternalServerException serviceUnavailable(String serviceName) {
        return new InternalServerException(ErrorCode.SERVICE_UNAVAILABLE,
            String.format("Le service %s est actuellement indisponible.", serviceName));
    }
    

    public static InternalServerException communicationError(String serviceName, String details) {
        return new InternalServerException(ErrorCode.COMMUNICATION_ERROR,
            String.format("Erreur de communication avec le service %s : %s", serviceName, details));
    }
}
