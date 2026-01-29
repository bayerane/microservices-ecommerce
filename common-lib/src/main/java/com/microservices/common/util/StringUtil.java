package com.microservices.common.util;

import java.text.Normalizer;
import java.util.Random;

/**
 * Classe utilitaire pour la manipulation de chaînes de caractères
 * 
 * @author Baye Rane
 * @version 1.0
 */
public class StringUtil {
    
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC = "0123456789";
    private static final Random RANDOM = new Random();

    private StringUtil() {
        throw new UnsupportedOperationException("Classe utilitaire, ne peut pas être instanciée.");
    }

    // Génère une chaîne alphanumérique aléatoire
    public static String generateRandomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    // Génère une chaîne numérique aléatoire
    public static String generateRandomNumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMERIC.charAt(RANDOM.nextInt(NUMERIC.length())));        
        }
        return sb.toString();
    }

    // Capitalise la première lettre d'une chaîne
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    // Capitalise la première lettre de chaque mot dans une chaîne
    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i >  0) {
                capitalized.append(" ");
            }
            capitalized.append(capitalize(words[i].toLowerCase()));
        }
        return capitalized.toString();
    }

    // Supprimer les accents d'une chaîne
    public static String removeAccents(String input) {
        if (input == null) {
            return null;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{M}]", "");
    }

    // Génère un slug à partir d'une chaîne
    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        String slug = removeAccents(input.toLowerCase());
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        slug = slug.trim().replaceAll("\\s+", "-");
        slug = slug.replaceAll("-+", "-");

        return slug;
    }

    // Masque partiellement une chaîne (ex: email, numéro de téléphone)
    public static String mask(String input, int unmaskedStart, int unmaskedEnd) {
        if (input == null || input.length() <= unmaskedStart + unmaskedEnd) {
            return input;
        }

        String start = input.substring(0, unmaskedStart);
        String end = input.substring(input.length() - unmaskedEnd);
        int maskedLength = input.length() - unmaskedStart - unmaskedEnd;

        return start + "*".repeat(maskedLength) + end;
    }

    // Masque un email
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***@" + domainPart;
        }

        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + "@" + domainPart;
    }

    // Tronque une chaîne à une longueur maximale
    public static String truncate(String input, int maxLength, String suffix) {
        if (input == null || input.length() <= maxLength) {
            return input;
        }

        int truncateAt = maxLength - suffix.length();
        if (truncateAt < 0) {
            truncateAt = maxLength;
        }
        return input.substring(0, truncateAt) + suffix;
    }

    // Vérifie si une chaîne contient uniquement des chiffres
    public static boolean isNumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return input.matches("\\d+");
    }

    // Vérifie si une chaîne contient uniquement des lettres
    public static boolean isAlphabetic(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return input.matches("[a-zA-Z]+");
    }

    // Compte le nombre d'occurences d'une sous-chaîne
    public static int countOccurrences(String input, String subString) {
        if (input == null || subString == null || subString.isEmpty()) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = input.indexOf(subString, index)) != -1) {
            count++;
            index += subString.length();
        }

        return count;
    }

    // Répète une chaîne un certain nombre de fois
    public static String repeat(String input, int times) {
        if (input == null || times <= 0) {
            return "";
        }

        return input.repeat(times);
    }

    // Inverse une chaîne
    public static String reverse(String input) {
        if (input == null) {
            return null;
        }

        return new StringBuilder(input).reverse().toString();
    }
}
