package com.company.project.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Reusable logging service with standardized log formats
 */
@Slf4j
@Service
public class LoggingService {
    
    public void logCreate(String resourceName, Object id) {
        log.info("Created {} with id: {}", resourceName, id);
    }
    
    public void logUpdate(String resourceName, Object id) {
        log.info("Updated {} with id: {}", resourceName, id);
    }
    
    public void logDelete(String resourceName, Object id) {
        log.info("Deleted {} with id: {}", resourceName, id);
    }
    
    public void logFetch(String resourceName, Object id) {
        log.debug("Fetching {} with id: {}", resourceName, id);
    }
    
    public void logFetchAll(String resourceName, int count) {
        log.debug("Fetched {} {} records", count, resourceName);
    }
    
    public void logError(String operation, String resourceName, Exception e) {
        log.error("Error during {} operation on {}: {}", operation, resourceName, e.getMessage(), e);
    }
    
    public void logWarning(String message, Object... args) {
        log.warn(message, args);
    }
}
