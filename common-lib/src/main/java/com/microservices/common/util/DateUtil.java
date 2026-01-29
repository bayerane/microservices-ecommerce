package com.microservices.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Classe utilitaire pour la manipulation des dates
 * 
 * @author Baye Rane
 * @version 1.0
 */
public final class DateUtil {
    
    // Formats standards
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FRENCH_DATE_FORMAT = "dd/MM/yyyy";
    public static final String FRENCH_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    // Formatters
    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_FORMAT);
    public static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATETIME_FORMAT);
    public static final DateTimeFormatter FRENCH_DATE_FORMATTER = DateTimeFormatter.ofPattern(FRENCH_DATE_FORMAT);
    public static final DateTimeFormatter FRENCH_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(FRENCH_DATETIME_FORMAT);

    private DateUtil() {
        throw new UnsupportedOperationException("Classe utilitaire ne peut pas être instanciée.");
    }

    // Récupère la date et l'heure actuelles
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // Récupère la date actuelle
    public static LocalDate today() {
        return LocalDate.now();
    }

    // Convertit LocalDateTime en String avec format ISO
    public static String formatIso(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FRENCH_DATETIME_FORMATTER) : null;
    }

    // Convertit LocalDate en String avec format ISO
    public static String formatIso(LocalDate date) {
        return date != null ? date.format(ISO_DATE_FORMATTER) : null;
    }

    // Convertit LocalDateTime en String avec format français
    public static String formatFrench(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FRENCH_DATETIME_FORMATTER) : null;
    }

    // Convertit LocalDate en String avec format français
    public static String formatFrench(LocalDate date) {
        return date != null ? date.format(FRENCH_DATE_FORMATTER) : null;
    }

    // Parse une chaîne ISO en LocalDateTime
    public static LocalDateTime parseIsoDateTime(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, ISO_DATETIME_FORMATTER) : null;
    }

    // Parse une chaîne ISO en LocalDate
    public static LocalDate parseIsoDate(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr, ISO_DATE_FORMATTER) : null;
    }

    // Calcule la différence en jours entre deux dates
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    // Calcule la différence en heures entre deux dates
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }

    // Vérifie si une date est passée
    public static boolean isPast(LocalDateTime date) {
        return date != null && date.isBefore(LocalDateTime.now());
    }

    // Vérifie si une date est future
    public static boolean isFuture(LocalDateTime date) {
        return date != null && date.isAfter(LocalDateTime.now());
    }

    // Convertit java.util.Date en LocalDateTime
    public static LocalDateTime toLocalDateTime(Date date) {
        return date != null ? date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime() : null;
    }

    // Convertit LocalDateTime en java.util.Date
    public static Date toDate(LocalDateTime dateTime) {
        return dateTime != null ? Date.from(dateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()) : null;
    }

    // Ajoute des jours à une date
    public static LocalDateTime addDays(LocalDateTime date, long days) {
        return date != null ? date.plusDays(days) : null;
    }

    // Ajoute des heures à une date
    public static LocalDateTime addHours(LocalDateTime date, long hours) {
        return date != null ? date.plusHours(hours) : null;
    }

    // Récupère le début de la journée (00:00:00)
    public static LocalDateTime startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    // Récupère la fin de la journée (23:59:59)
    public static LocalDateTime endOfDay(LocalDate date) {
        return date != null ? date.atTime(23, 59, 59) : null;
    }
}
