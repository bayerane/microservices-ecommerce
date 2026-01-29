package com.microservices.common.enums;

/**
 * Énumération des codes d'erreur standardisés
 * 
 * @author Baye Rane
 * @version 1.0
 */
public enum ErrorCode {
    
    // Erreurs génériques (1000-1999)
    INTERNAL_SERVER_ERROR("ERR-1000", "Erreur interne du serveur"),
    INVALID_REQUEST("ERR-1001", "Requête invalide"),
    VALIDATION_ERROR("ERR-1002", "Erreur de validation"),
    RESOURCE_NOT_FOUND("ERR-1003", "Ressource non trouvée"),

    // Erreurs d'authentification (2000-2999)
    AUTHENTICATION_FAILED("ERR-2000", "Échec de l'authentification"),
    INVALID_CREDENTIALS("ERR-2001", "Identifiants invalides"),
    TOKEN_EXPIRED("ERR-2002", "Token expiré"),
    TOKEN_INVALID("ERR-2003", "Token invalide"),
    UNAUTHORIZED_ACCESS("ERR-2004", "Accès non autorisé"),

    // Erreurs d'autorisation (3000-3999)
    ACCESS_DENIED("ERR-3000", "Accès refusé"),
    INSUFFICIENT_PERMISSIONS("ERR-3001", "Permissions insuffisantes"),
    FORBIDDEN_OPERATION("ERR-3002", "Opération interdite"),

    // Erreurs utilisateur (4000-4999)
    USER_NOT_FOUND("ERR-4000", "Utilisateur non trouvé"),
    USER_ALREADY_EXISTS("ERR-4001", "L'utilisateur existe déjà"),
    USER_DISABLED("ERR-4002", "L'utilisateur est déactivé"),
    INVALID_EMAIL("ERR-4003", "Email invalide"),
    WEAK_PASSWORD("ERR-4004", "Mot de passe trop faible"),
    PASSWORD_MISMATCH("ERR-4005", "Les mots de passe ne correspondent pas"),

    // Erreurs commande (5000-5999)
    ORDER_NOT_FOUND("ERR-5000", "Commande non trouvée"),
    ORDER_ALREADY_CANCELLED("ERR-5001", "Commande déjà annulée"),
    ORDER_CANNOT_BE_CANCELLED("ERR-5002", "La commande ne peut pas être annulée"),
    INVALID_ORDER_STATUS("ERR-5003", "Statut de commande invalide"),
    INVALID_ORDER_AMOUNT("ERR-5004", "Montant de commande invalide"),
    ORDER_STATUS_TRANSITION_INVALID("ERR-5005", "Transition de statut invalide"),

    // Erreurs de base de données (6000-6999)
    DATABASE_ERROR("ERR-6000", "Erreur de base de données"),
    DUPLICATE_ENTRY("ERR-6001", "Entrée dupliquée"),
    CONTRAINT_VIOLATION("ERR-6002", "Violation de contrainte"),

    // Erreurs de communication inter-services (7000-7999)
    SERVICE_UNAVAILABLE("ERR-7000", "Service indisponible"),
    SERVICE_TIMEOUT("ERR-7001", "Timeout du service"),
    COMMUNICATION_ERROR("ERR-7002", "Erreur de communication");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // Récupère le code d'erreur
    public String getCode() {
        return code;
    }

    // Récupère le message d'erreur
    public String getMessage() {
        return message;
    }

    // Récupère un code d'erreur à partir de son code
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
