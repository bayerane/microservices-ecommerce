package com.microservices.common.util;

import java.util.regex.Pattern;

/**
 * Classe utilitaire pour la validation des données
 * 
 * @author Baye Rane
 * @version 1.0
 */
public final class ValidationUtil {
    
    // Patterns de validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?\\d{7,15}$"
    );

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    // Exigences de mot de passe
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+=|<>?{}\\[\\]~-]");

    private ValidationUtil() {
        throw new UnsupportedOperationException("Classe utilitaire, ne peut pas être instanciée.");
    }

    // Valide une adresse e-mail
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    // Valide un numéro de téléphone
    public static boolean isValidPhone(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber.replace("\\s", "")).matches();
    }

    // Valide un UUID
    public static boolean isValidUUID(String uuid) {
        return uuid != null && UUID_PATTERN.matcher(uuid).matches();
    }

    // Valide la force d'un mot de passe
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        return UPPERCASE_PATTERN.matcher(password).find() &&
            LOWERCASE_PATTERN.matcher(password).find() &&
            DIGIT_PATTERN.matcher(password).find();
    }

    // Valide la force d'un mot de passe avec caractères spéciaux
    public static boolean isVeryStrongPassword(String password) {
        return isStrongPassword(password) &&
            SPECIAL_CHAR_PATTERN.matcher(password).find();
    }

    // Récupère un message décrivant les exigences du mot de passe
    public static String getPasswordRequirements() {
        return String.format(
            "Le mot de passe doit contenir au moins %d caractères, incluant: " +
            "une majuscule, une minuscule, et un chiffre.",
            MIN_PASSWORD_LENGTH
        );
    }

    // Vérifie si une chaîne est nulle ou vide
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Vérifie si une chaîne n'est ni nulle ni vide
    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    // Valide qu'une valeur numérique est positive
    public static boolean isPositive(Number value) {
        return value != null && value.doubleValue() > 0;
    }

    // Valide qu'une valeur numérique est positive ou nulle
    public static boolean isPositiveOrZero(Number value) {
        return value != null && value.doubleValue() >= 0;
    }

    // Valide qu'une chaîne a une longueur dans la plage spécifiée
    public static boolean isLengthBetween(String str, int min, int max) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= min && length <= max;
    }

    // Nettoie et normalise un email
    public static String normalizeEmail(String email) {
        return email != null ? email.trim().toLowerCase() : null;
    }

    // Valide un code postal
    public static boolean isValidPostalCode(String postalCode) {
        if (postalCode == null) {
            return false;
        }
        String trimmedCode = postalCode.replaceAll("\\s", "");
        return trimmedCode.matches("^[0-9A-Za-z]{3,10}$");
    }

    // Valide qu'une valeur est dans une plage
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
