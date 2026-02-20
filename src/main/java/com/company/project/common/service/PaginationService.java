package com.company.project.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reusable pagination service for handling paginated responses
 */
@Service
public class PaginationService {
    
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    
    public Pageable createPageable(int page, int size, String sortBy, String direction) {
        int validatedSize = Math.min(size > 0 ? size : DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE);
        Sort sort = createSort(sortBy, direction);
        return PageRequest.of(page, validatedSize, sort);
    }
    
    public Pageable createPageable(int page, int size) {
        return createPageable(page, size, null, "ASC");
    }
    
    private Sort createSort(String sortBy, String direction) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.unsorted();
        }
        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        return Sort.by(sortDirection, sortBy);
    }
    
    public <T> PageResponse<T> createPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
    
    public record PageResponse<T>(
            List<T> content,
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {}
}
