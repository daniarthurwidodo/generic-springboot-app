package com.company.project.common.util;

/**
 * Reusable utility class for string operations
 */
public class StringUtil {
    
    private StringUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
    
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
    
    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }
}
