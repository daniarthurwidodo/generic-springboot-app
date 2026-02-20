package com.company.project.common.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reusable validation service for programmatic validation
 */
@Service
@RequiredArgsConstructor
public class ValidationService {
    
    private final Validator validator;
    
    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Validation failed: " + errors);
        }
    }
    
    public <T> boolean isValid(T object) {
        return validator.validate(object).isEmpty();
    }
}
