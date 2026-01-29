package com.microservices.common.constant;

/**
 * Constantes de l'application
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class AppConstants {
    
    private AppConstants() {
        throw new UnsupportedOperationException("Classe de constantes, ne peut pas être instanciée.");
    }

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Validation
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_PHONE_LENGTH = 20;
    public static final int MAX_ADDRESS_LENGTH = 500;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;

    // Formats de date
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";

    // Messages
    public static final String SUCCESS_MESSAGE = "Opération réussie";
    public static final String CREATED_MESSAGE = "Créé avec succès";
    public static final String UPDATED_MESSAGE = "Mis à jour avec succès";
    public static final String DELETED_MESSAGE = "Supprimé avec succès";
    public static final String NOT_FOUND_MESSAGE = "Ressource non trouvée";
    public static final String UNAUTHORIZED_MESSAGE = "Non autorisé";
    public static final String FORBIDDEN_MESSAGE = "Accès interdit";
    public static final String BAD_REQUEST_MESSAGE = "Requête invalide";
    public static final String INTERNAL_ERROR_MESSAGE = "Erreur interne du serveur";

    // Timeouts (en millisecondes)
    public static final long DEFAULT_REQUEST_TIMEOUT = 5000; // 5 secondes
    public static final long FEIGN_CONNECT_TIMEOUT = 5000;
    public static final long FEIGN_READ_TIMEOUT = 10000;

    // Circuit Breaker
    public static final int CIRCUIT_BREAKER_FAILURE_THRESHOLD = 50; // 50%
    public static final int CIRCUIT_BREAKER_WAIT_DURATION = 30000; // 30 secondes
    public static final int CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE = 10;

    // Cache
    public static final String CACHE_USERS = "users";
    public static final String CACHE_ORDERS = "orders";
    public static final long CACHE_TTL_MINUTES = 30;

    // API Versioning
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api";

    // Encoding
    public static final String DEFAULT_ENCODING = "UTF-8";

    // Regex patterns
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_REGEX = "^\\\\+?[0-9]{7,15}$";
    public static final String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    // Order
    public static final String ORDER_NUMBER_PREFIX = "ORD-";
    public static final int ORDER_NUMBER_LENGTH = 15;

    // Currency
    public static final String DEFAULT_CURRENCY = "EUR";
    public static final String CURRENCY_SYMBOL = "€";

    // Zone ID
    public static final String DEFAULT_ZONE_ID = "Europe/Paris";
}
