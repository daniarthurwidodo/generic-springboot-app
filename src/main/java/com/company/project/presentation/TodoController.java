package com.company.project.presentation;

import com.company.project.application.TodoService;
import com.company.project.domain.Todo;
import com.company.project.dto.TodoRequest;
import com.company.project.dto.TodoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sql/todo")
@Tag(name = "Todo", description = "Todo management API")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    @Operation(summary = "Create a new todo", description = "Creates a new todo item with the provided title and description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Todo created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - title is required")
    })
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        log.info("Received request to create todo: {}", request.title());
        Todo todo = todoService.createTodo(request.title(), request.description());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(todo.getId())
                .toUri();
        log.info("Created todo at location: {}", location);
        return ResponseEntity.created(location).body(toResponse(todo));
    }

    @GetMapping
    @Operation(summary = "Get all todos", description = "Retrieves a list of all todo items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of todos")
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        log.debug("Received request to get all todos");
        List<Todo> todos = todoService.getAllTodos();
        log.debug("Returning {} todos", todos.size());
        return ResponseEntity.ok(todos.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get todo by ID", description = "Retrieves a specific todo item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo found"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    public ResponseEntity<TodoResponse> getTodoById(
            @Parameter(description = "The unique ID of the todo") @PathVariable Long id) {
        log.debug("Received request to get todo by id: {}", id);
        return todoService.getTodoById(id)
                .map(todo -> ResponseEntity.ok(toResponse(todo)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a todo", description = "Updates an existing todo item with new title and description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    public ResponseEntity<TodoResponse> updateTodo(
            @Parameter(description = "The unique ID of the todo") @PathVariable Long id,
            @Valid @RequestBody TodoRequest request) {
        log.info("Received request to update todo with id: {}", id);
        try {
            Todo todo = todoService.updateTodo(id, request.title(), request.description());
            return ResponseEntity.ok(toResponse(todo));
        } catch (IllegalArgumentException e) {
            log.warn("Todo not found for update with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle todo completion", description = "Toggles the completion status of a todo item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo completion status toggled"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    public ResponseEntity<TodoResponse> toggleTodoCompletion(
            @Parameter(description = "The unique ID of the todo") @PathVariable Long id) {
        log.info("Received request to toggle todo completion with id: {}", id);
        try {
            Todo todo = todoService.toggleTodoCompletion(id);
            return ResponseEntity.ok(toResponse(todo));
        } catch (IllegalArgumentException e) {
            log.warn("Todo not found for toggle with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a todo", description = "Deletes a todo item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todo deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "The unique ID of the todo") @PathVariable Long id) {
        log.info("Received request to delete todo with id: {}", id);
        try {
            todoService.deleteTodo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Todo not found for delete with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
