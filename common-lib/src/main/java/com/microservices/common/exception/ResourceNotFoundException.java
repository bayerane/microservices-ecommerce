package com.microservices.common.exception;

import com.microservices.common.enums.ErrorCode;

/**
 * Exception levée lorsqu'une ressource n'est pas trouvée
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
            String.format("%s non trouvé avec %s : '%s'", resourceName, fieldName, fieldValue)
        );
    }

    public ResourceNotFoundException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }

    public static ResourceNotFoundException user(String userId) {
        return new ResourceNotFoundException("Utilisateur", "id", userId);
    }

    public static ResourceNotFoundException userByEmail(String email) {
        return new ResourceNotFoundException("Utilisateur", "email", email);
    }

    public static ResourceNotFoundException order(String orderId) {
        return new ResourceNotFoundException("Commande", "id", orderId);
    }

    public static ResourceNotFoundException orderByNumber(String orderNumber) {
        return new ResourceNotFoundException("Commande", "numéro", orderNumber);
    }
    
}
