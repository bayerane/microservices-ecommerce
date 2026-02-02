package com.microservices.auth.exception;

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
import com.microservices.common.exception.UnauthorizedException;

import lombok.extern.slf4j.Slf4j;

/**
 * Gestionnaire global des exceptions pour Auth Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    // Gére les exceptions de ressource non trouvée
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
        ResourceNotFoundException ex,
        WebRequest request
    ) {
        log.warn("Ressource non trouvée: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getErrorCode(), ex.getDetails());
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Gére les exceptions d'authentification
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
        UnauthorizedException ex,
        WebRequest request
    ) {
        log.warn("Erreur d'authentification: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), ex.getErrorCode(), ex.getDetails());
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Gére les exceptions d'autorisation
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
        ForbiddenException ex,
        WebRequest request
    ) {
        log.warn("Accès refusé: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(HttpStatus.FORBIDDEN.value(), ex.getErrorCode(), ex.getDetails());
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

        ErrorResponse error = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getErrorCode(), ex.getDetails());
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
        
        // Ajouter toutes les erreurs de champ
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

    // Gère les exceptions métier génériques
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
        BusinessException ex,
        WebRequest request
    ) {
        log.warn("Erreur metier: {}", ex.getMessage());

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
    public ResponseEntity<ErrorResponse> handleException(
        Exception ex,
        WebRequest request
    ) {
        log.error("Erreur non gérée: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ErrorCode.INTERNAL_SERVER_ERROR,
            "Une erreur inattendue s'est produite"
        );
        error.setPath(getRequestPath(request));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Extrait le chemin de la requête
    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getRequestURI();
        }
        return request.getDescription(false);
    }
}
