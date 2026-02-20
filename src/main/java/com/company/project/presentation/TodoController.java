package com.company.project.presentation;

import com.company.project.application.TodoMapperService;
import com.company.project.application.TodoService;
import com.company.project.common.controller.BaseController;
import com.company.project.common.util.ApiResponses;
import com.company.project.domain.Todo;
import com.company.project.dto.TodoRequest;
import com.company.project.dto.TodoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/sql/todo")
@Tag(name = "Todo", description = "Todo management API")
public class TodoController extends BaseController<Todo, TodoRequest, TodoResponse, String> {

    private final TodoService todoService;

    public TodoController(TodoService todoService, TodoMapperService mapperService) {
        super(todoService, mapperService, "Todo");
        this.todoService = todoService;
    }

    @PostMapping
    @Operation(summary = "Create a new todo", description = "Creates a new todo item with the provided title and description")
    @ApiResponses.TodoCreated
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        return create(request);
    }

    @GetMapping
    @Operation(summary = "Get all todos", description = "Retrieves a list of all todo items")
    @ApiResponses.TodoListRetrieved
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        return findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get todo by ID", description = "Retrieves a specific todo item by its ID")
    @ApiResponses.TodoFound
    public ResponseEntity<TodoResponse> getTodoById(
            @Parameter(description = "The unique ID of the todo") @PathVariable String id) {
        return findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a todo", description = "Updates an existing todo item with new title and description")
    @ApiResponses.TodoUpdated
    public ResponseEntity<TodoResponse> updateTodo(
            @Parameter(description = "The unique ID of the todo") @PathVariable String id,
            @Valid @RequestBody TodoRequest request) {
        return update(id, request);
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle todo completion", description = "Toggles the completion status of a todo item")
    @ApiResponses.TodoFound
    public ResponseEntity<TodoResponse> toggleTodoCompletion(
            @Parameter(description = "The unique ID of the todo") @PathVariable String id) {
        log.info("Received request to toggle todo completion with id: {}", id);
        Todo todo = todoService.toggleCompletion(id);
        return ResponseEntity.ok(mapper.toResponse(todo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a todo", description = "Deletes a todo item by its ID")
    @ApiResponses.TodoDeleted
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "The unique ID of the todo") @PathVariable String id) {
        return delete(id);
    }

    @Override
    protected Object getEntityId(Todo entity) {
        return entity.getId();
    }
}
