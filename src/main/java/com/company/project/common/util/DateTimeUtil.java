package com.company.project.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Reusable utility class for date and time operations
 */
public class DateTimeUtil {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private DateTimeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
    
    public static String formatIso(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_FORMATTER) : null;
    }
    
    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMATTER) : null;
    }
    
    public static LocalDateTime parseIso(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, ISO_FORMATTER) : null;
    }
    
    public static boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1 != null && dateTime2 != null && dateTime1.isBefore(dateTime2);
    }
    
    public static boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1 != null && dateTime2 != null && dateTime1.isAfter(dateTime2);
    }
}
