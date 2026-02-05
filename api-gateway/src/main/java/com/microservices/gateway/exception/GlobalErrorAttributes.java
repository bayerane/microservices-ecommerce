package com.microservices.gateway.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Gestionnaire global des attributs d'erreur pour la Gateway
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Component
@Slf4j
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        Throwable error = getError(request);

        log.error(
            "Gateway error occurred: {} - Path: {}",
            error.getMessage(),
            request.path()
        );

        // Personnalisation des attributs d'erreur
        errorAttributes.put("succes", false);
        errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        errorAttributes.put("path", request.path());

        // Déterminer le message d'erreur approprié
        String message = determineErrorMessage(error, errorAttributes);
        errorAttributes.put("message", message);
        
        // Supprimer les informations sensibles en production
        errorAttributes.remove("trace");
        errorAttributes.remove("exception");

        return errorAttributes;
    }

    // Détermine le message d'erreur approprié selon le type d'erreur
    private String determineErrorMessage(Throwable error, Map<String, Object> attributes) {
        Integer status = (Integer) attributes.get("status");

        if (status != null) {
            HttpStatus httpStatus = HttpStatus.resolve(status);
            if (httpStatus != null) {
                return switch (httpStatus) {
                    case UNAUTHORIZED -> "Authentification requise";
                    case FORBIDDEN -> "Accès refusé";
                    case NOT_FOUND -> "Ressource non trouvée";
                    case SERVICE_UNAVAILABLE -> "Service temporairement indisponible";
                    case GATEWAY_TIMEOUT -> "Délai d'attente dépassé";
                    case BAD_REQUEST -> "Requête invalide";
                    default -> httpStatus.getReasonPhrase();
                };
            }
        }

        return error != null ? error.getMessage() : "Erreur interne du serveur";
    }
}
