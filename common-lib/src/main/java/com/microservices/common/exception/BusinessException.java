package com.microservices.common.exception;

import com.microservices.common.enums.ErrorCode;
/**
 * Exception de base pour toutes les erreurs métier
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String details;

    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        this.details = null;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        this.details = null;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public BusinessException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

    public BusinessException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "errorCode=" + errorCode +
                ", message='" + getMessage() + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
