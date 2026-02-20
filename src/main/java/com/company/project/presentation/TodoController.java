package com.company.project.presentation;

import com.company.project.application.TodoService;
import com.company.project.domain.Todo;
import com.company.project.dto.ErrorResponse;
import com.company.project.dto.TodoRequest;
import com.company.project.dto.TodoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @ApiResponses.TodoCreated
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
    @ApiResponses.TodoListRetrieved
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        log.debug("Received request to get all todos");
        List<Todo> todos = todoService.getAllTodos();
        log.debug("Returning {} todos", todos.size());
        return ResponseEntity.ok(todos.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get todo by ID", description = "Retrieves a specific todo item by its ID")
    @ApiResponses.TodoFound
    public ResponseEntity<?> getTodoById(
            @Parameter(description = "The unique ID of the todo") @PathVariable Long id) {
        log.debug("Received request to get todo by id: {}", id);
        var todo = todoService.getTodoById(id);
        if (todo.isPresent()) {
            return ResponseEntity.ok(toResponse(todo.get()));
        } else {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Todo not found", "Todo with the specified ID does not exist"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a todo", description = "Updates an existing todo item with new title and description")
    @ApiResponses.TodoUpdated
    public ResponseEntity<?> updateTodo(
            @Parameter(description = "The unique ID of the todo") @PathVariable Long id,
            @Valid @RequestBody TodoRequest request) {
        log.info("Received request to update todo with id: {}", id);
        try {
            Todo todo = todoService.updateTodo(id, request.title(), request.description());
            return ResponseEntity.ok(toResponse(todo));
        } catch (IllegalArgumentException e) {
            log.warn("Todo not found for update with id: {}", id);
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Todo not found", "Todo with the specified ID does not exist"));
        }
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle todo completion", description = "Toggles the completion status of a todo item")
    @ApiResponses.TodoFound
    public ResponseEntity<?> toggleTodoCompletion(
            @Parameter(description = "The unique ID of the todo") @PathVariable Long id) {
        log.info("Received request to toggle todo completion with id: {}", id);
        try {
            Todo todo = todoService.toggleTodoCompletion(id);
            return ResponseEntity.ok(toResponse(todo));
        } catch (IllegalArgumentException e) {
            log.warn("Todo not found for toggle with id: {}", id);
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Todo not found", "Todo with the specified ID does not exist"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a todo", description = "Deletes a todo item by its ID")
    @ApiResponses.TodoDeleted
    public ResponseEntity<?> deleteTodo(
            @Parameter(description = "The unique ID of the todo") @PathVariable Long id) {
        log.info("Received request to delete todo with id: {}", id);
        try {
            todoService.deleteTodo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Todo not found for delete with id: {}", id);
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Todo not found", "Todo with the specified ID does not exist"));
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
