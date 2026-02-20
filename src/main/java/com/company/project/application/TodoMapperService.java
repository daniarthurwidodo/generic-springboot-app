package com.company.project.application;

import com.company.project.common.service.MapperService;
import com.company.project.domain.Todo;
import com.company.project.dto.TodoRequest;
import com.company.project.dto.TodoResponse;
import org.springframework.stereotype.Service;

/**
 * Mapper service for Todo entity conversions
 */
@Service
public class TodoMapperService implements MapperService<Todo, TodoRequest, TodoResponse> {
    
    @Override
    public Todo toEntity(TodoRequest request) {
        return new Todo(request.title(), request.description());
    }
    
    @Override
    public TodoResponse toResponse(Todo entity) {
        return new TodoResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.isCompleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
