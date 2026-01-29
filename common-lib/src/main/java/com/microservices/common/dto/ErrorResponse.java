package com.microservices.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.microservices.common.enums.ErrorCode;

/**
 * Classe pour les réponses d'erreur standardisées
 * 
 * @author Baye Rane
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private int status;
    private String errorCode;
    private String message;
    private String details;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String errorCode, String message) {
        this();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(int status, String errorCode, String message, String details) {
        this(status, errorCode, message);
        this.details = details;
    }

    public ErrorResponse(int status, ErrorCode errorCode , String details) {
        this(status, errorCode.getCode(), errorCode.getMessage(), details);
    }

    // Crée une ErrorResponse à partir d'un ErrorCode
    public static ErrorResponse of(int status, ErrorCode errorCode) {
        return new ErrorResponse(status, errorCode.getCode(), errorCode.getMessage());
    }

    // Crée une ErrorResponse à partir d'un ErrorCode avec détails
    public static ErrorResponse of(int status, ErrorCode errorCode, String details) {
        return new ErrorResponse(status, errorCode.getCode(), errorCode.getMessage(), details);
    }

    // Crée une ErrorResponse personnalisée
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, "ERROR", message);
    }

    // Getters et Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status=" + status +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
