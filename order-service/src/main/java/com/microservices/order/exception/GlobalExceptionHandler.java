package com.microservices.order.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.microservices.common.dto.ApiResponse;
import com.microservices.common.exception.BusinessException;
import com.microservices.common.exception.ResourceNotFoundException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestionnaire global des exceptions pour Order Service
 * 
 * @author Baye Rane
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    // Gestion des erreurs de validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions (
            MethodArgumentNotValidException ex) {

        log.warn("Erreur de validation: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                "Erreur de validation",
                errors
            ));
    }

    // Gestion des ResourceNotFoundException (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlerResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        log.warn(
            "Ressource introuvable: {} - Path: {}",
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }

    // Gestion des BusinessException (400)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handlerBusinessException(
            BusinessException ex,
            WebRequest request) {
        
        log.warn(
            "Business exception: {} - Path: {}",
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.getMessage()));
    }

    // Gestion des erreurs d'authentification (401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handlerAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {
        
        log.warn(
            "Erreur d'authentification: {} - Path: {}",
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(
                "Authentification requise",
                "Veuillez vous connecter pour accèder à cette ressource"
            ));
    }

    // Gestion des erreurs d'autorisation (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handlerAuthorizationException(
            AccessDeniedException ex,
            WebRequest request) {
        
        log.warn(
            "Accès refusé: {} - Path: {}",
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(
                "Accès refusé",
                "Vous n'avez pas les permissions nécessaires pour cette opération"
            ));
    }

    // Gestion des erreurs Feign (communication inter-services)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<String>> handlerFeignException(
            FeignException ex,
            WebRequest request) {
        
        log.warn(
            "Erreur de communication: {} - status: {} - Path: {}",
            ex.getMessage(),
            ex.status(),
            request.getDescription(false)
        );

        String message;
        HttpStatus status;

        switch (ex.status()) {
            case 404:
                message = "Ressource introuvable";
                status = HttpStatus.NOT_FOUND;
                break;
            case 503:
                message = "Service temporairement indisponible";
                status = HttpStatus.SERVICE_UNAVAILABLE;
                break;
            default:
                message = "Erreur de communication";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }

        return ResponseEntity
            .status(status)
            .body(ApiResponse.error(message, ex.getMessage()));
    }

    // Gestion des IllegalArgumentException (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handlerIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        
        log.warn(
            "IllegalArgumentException: {} - Path: {}",
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                "Argument invalide",
                ex.getMessage()
            ));
    }

    // Gestion des exceptions non gérées (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handlerGlobalException(
            Exception ex,
            WebRequest request) {
        
        log.error(
            "Exception non gérée: {} - Path: {}",
            ex.getMessage(),
            request.getDescription(false),
            ex
        );

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getDescription(false));
        errorDetails.put("error", ex.getClass().getSimpleName());

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(
                "Une erreur interne s'est produite",
                errorDetails
            ));
    }
}
