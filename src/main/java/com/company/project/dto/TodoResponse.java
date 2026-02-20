package com.company.project.dto;

import java.time.LocalDateTime;

public record TodoResponse(
        String id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
