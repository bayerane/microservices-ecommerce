package com.microservices.common.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Classe pour les réponses d'erreur de validation
 * 
 * @author Baye Rane
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationErrorResponse {
    
    private int status;
    private String message;
    private List<FieldError> errors;
    private String path;
    private LocalDateTime timestamp;

    public ValidationErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
    }

    public ValidationErrorResponse(int status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    // Ajoute une erreur de champ
    public void addFieldError(String field, String message) {
        this.errors.add(new FieldError(field, message));
    }

    // Ajoute une erreur de champ avec la valeur rejetée
    public void addFieldError(String field, String message, Object rejectedValue) {
        this.errors.add(new FieldError(field, message, rejectedValue));
    }

    // Classe interne pour représenter une erreur de champ
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;

        public FieldError() {}

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        // Getters et Setters
        public String getField() {
            return field;    
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        @Override
        public String toString() {
            return "FieldError{" +
                    "field='" + field + '\'' +
                    ", message='" + message + '\'' +
                    ", rejectedValue=" + rejectedValue +
                    '}';
        }
    }

    // Getters et Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
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
        return "ValidationErrorResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
