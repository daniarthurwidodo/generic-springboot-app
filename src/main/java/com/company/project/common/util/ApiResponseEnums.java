package com.company.project.common.util;

/**
 * Centralized API response examples for OpenAPI documentation
 * Reusable across all controllers
 */
public final class ApiResponseEnums {
    
    private ApiResponseEnums() {
        // Prevent instantiation
    }
    
    // ========== Todo-Specific Success Responses ==========
    
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
    
    // ========== Todo-Specific Error Responses ==========
    
    public static final String TODO_NOT_FOUND_ERROR =
            "{\"error\": \"Todo not found\", \"message\": \"Todo with the specified ID does not exist\"}";
    
    public static final String VALIDATION_ERROR =
            "{\"error\": \"Validation failed\", \"message\": \"Title is required and cannot be empty\"}";
    
    // ========== Generic Error Responses ==========
    
    public static final String RESOURCE_NOT_FOUND_ERROR =
            "{\"error\": \"Resource not found\", \"message\": \"The requested resource does not exist\"}";
    
    public static final String UNAUTHORIZED_ERROR =
            "{\"error\": \"Unauthorized\", \"message\": \"Authentication is required to access this resource\"}";
    
    public static final String FORBIDDEN_ERROR =
            "{\"error\": \"Forbidden\", \"message\": \"You do not have permission to access this resource\"}";
    
    public static final String INTERNAL_SERVER_ERROR =
            "{\"error\": \"Internal server error\", \"message\": \"An unexpected error occurred\"}";
    
    public static final String BAD_REQUEST_ERROR =
            "{\"error\": \"Bad request\", \"message\": \"The request could not be understood or was missing required parameters\"}";
}
