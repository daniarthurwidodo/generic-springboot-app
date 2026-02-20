package com.company.project.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Reusable service for building standardized API responses
 */
@Service
public class ResponseBuilderService {
    
    public <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, data, null));
    }
    
    public <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(new ApiResponse<>(true, data, message));
    }
    
    public <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, data, "Resource created successfully"));
    }
    
    public ResponseEntity<ApiResponse<Void>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ApiResponse<>(true, null, "Operation completed successfully"));
    }
    
    public <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(false, null, message));
    }
    
    public ResponseEntity<Map<String, Object>> buildHealthResponse(String status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    public record ApiResponse<T>(boolean success, T data, String message) {}
}
