package com.company.project.dto;

/**
 * Error response DTO for API error responses
 */
public record ErrorResponse(
        String error,
        String message
) {
}
