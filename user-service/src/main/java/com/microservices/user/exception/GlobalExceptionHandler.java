package com.microservices.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.microservices.common.dto.ErrorResponse;
import com.microservices.common.dto.ValidationErrorResponse;
import com.microservices.common.enums.ErrorCode;
import com.microservices.common.exception.BadRequestException;
import com.microservices.common.exception.BusinessException;
import com.microservices.common.exception.ForbiddenException;
import com.microservices.common.exception.ResourceNotFoundException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestionnaire global des exceptions pour User Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    // Gère les exceptions de ressource non trouvée
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
        ResourceNotFoundException ex,
        WebRequest request
    ) {
        log.warn("Ressource non trouvée: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            ex.getErrorCode(),
            ex.getDetails()
        );
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Gère les exceptions d'authorisation
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiden(
        ForbiddenException ex,
        WebRequest request
    ) {
        log.warn("Accès refusé: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            ex.getErrorCode(),
            ex.getDetails()
        );
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // Gère les exceptions de requête invalide
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
        BadRequestException ex,
        WebRequest request
    ) {
        log.warn("Requête invalide: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            ex.getErrorCode(),
            ex.getDetails()
        );
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Gère les exceptions de validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex,
        WebRequest request
    ) {
        log.warn("Erreur de validation: {} erreur(s)", ex.getBindingResult().getErrorCount());
        
        ValidationErrorResponse response = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Erreur de validation"
        );

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            response.addFieldError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
            );
        }

        response.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Gère les exceptions Feign (communication avec d'autres services)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(
        FeignException ex,
        WebRequest request
    ) {
        log.error("Erreur de communication avec un autre ssrvice", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            ex.status(),
            ErrorCode.COMMUNICATION_ERROR,
            "Erreur de communication avec un service distant"
        );
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(ex.status()).body(error);
    }

    // Gère les exceptions métier génériques
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
        BusinessException ex,
        WebRequest request
    ) {
        log.error("Erreur métier: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getErrorCode(),
            ex.getDetails()
        );
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Gère les exceptions non capturées
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex,
        WebRequest request
    ) {
        log.error("Erreur non gérée: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ErrorCode.INTERNAL_SERVER_ERROR,
            "Une erreur inattendue s'est produite"
        );
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Extrait le chemin de la rrequête
    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getRequestURI();
        }
        return request.getDescription(false);
    }
}
