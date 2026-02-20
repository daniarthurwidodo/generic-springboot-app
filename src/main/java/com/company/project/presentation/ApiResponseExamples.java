package com.company.project.presentation;

/**
 * Centralized API response examples for OpenAPI documentation
 */
public final class ApiResponseExamples {
    
    private ApiResponseExamples() {
        // Prevent instantiation
    }
    
    // Success responses (IDs and timestamps are dynamically generated)
    public static final String TODO_CREATED =
            "{\"id\": \"<generated>\", \"title\": \"New Task\", \"description\": \"Task description\", " +
            "\"completed\": false, \"createdAt\": \"<timestamp>\", \"updatedAt\": \"<timestamp>\"}";
    
    public static final String TODO_FOUND =
            "{\"id\": \"<id>\", \"title\": \"Sample Task\", \"description\": \"Task description\", " +
            "\"completed\": false, \"createdAt\": \"<timestamp>\", \"updatedAt\": \"<timestamp>\"}";
    
    public static final String TODO_UPDATED =
            "{\"id\": \"<id>\", \"title\": \"Updated Task\", \"description\": \"Updated description\", " +
            "\"completed\": false, \"createdAt\": \"<timestamp>\", \"updatedAt\": \"<timestamp>\"}";
    
    public static final String TODO_LIST =
            "[{\"id\": \"<id1>\", \"title\": \"First Task\", \"description\": \"Description 1\", " +
            "\"completed\": false, \"createdAt\": \"<timestamp>\", \"updatedAt\": \"<timestamp>\"}, " +
            "{\"id\": \"<id2>\", \"title\": \"Second Task\", \"description\": \"Description 2\", " +
            "\"completed\": true, \"createdAt\": \"<timestamp>\", \"updatedAt\": \"<timestamp>\"}]";
    
    // Error responses
    public static final String TODO_NOT_FOUND_ERROR =
            "{\"error\": \"Todo not found\", \"message\": \"Todo with the specified ID does not exist\"}";
    
    public static final String VALIDATION_ERROR =
            "{\"error\": \"Validation failed\", \"message\": \"Title is required and cannot be empty\"}";
}
