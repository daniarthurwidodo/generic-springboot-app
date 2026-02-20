package com.company.project.presentation;

import com.company.project.application.McpToolsService;
import com.company.project.application.TodoService;
import com.company.project.domain.Todo;
import com.company.project.dto.TodoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private McpToolsService mcpToolsService;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createTodoShouldReturnCreatedTodo() throws Exception {
        TodoRequest request = new TodoRequest("Test Todo", "Test Description");
        Todo savedTodo = new Todo(1L, "Test Todo", "Test Description", false, LocalDateTime.now(), LocalDateTime.now());

        given(todoService.createTodo(anyString(), anyString())).willReturn(savedTodo);

        mockMvc.perform(post("/api/v1/sql/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Todo"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @WithMockUser
    void createTodoWithBlankTitleShouldReturnBadRequest() throws Exception {
        TodoRequest request = new TodoRequest("", "Test Description");

        mockMvc.perform(post("/api/v1/sql/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAllTodosShouldReturnTodoList() throws Exception {
        List<Todo> todos = List.of(
                new Todo(1L, "Todo 1", "Description 1", false, LocalDateTime.now(), LocalDateTime.now()),
                new Todo(2L, "Todo 2", "Description 2", true, LocalDateTime.now(), LocalDateTime.now())
        );

        given(todoService.getAllTodos()).willReturn(todos);

        mockMvc.perform(get("/api/v1/sql/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Todo 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Todo 2"));
    }

    @Test
    @WithMockUser
    void getTodoByIdShouldReturnTodo() throws Exception {
        Todo todo = new Todo(1L, "Test Todo", "Test Description", false, LocalDateTime.now(), LocalDateTime.now());

        given(todoService.getTodoById(1L)).willReturn(Optional.of(todo));

        mockMvc.perform(get("/api/v1/sql/todo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    @WithMockUser
    void getTodoByIdNotFoundShouldReturnNotFound() throws Exception {
        given(todoService.getTodoById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/sql/todo/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateTodoShouldReturnUpdatedTodo() throws Exception {
        TodoRequest request = new TodoRequest("Updated Todo", "Updated Description");
        Todo updatedTodo = new Todo(1L, "Updated Todo", "Updated Description", false, LocalDateTime.now(), LocalDateTime.now());

        given(todoService.updateTodo(eq(1L), anyString(), anyString())).willReturn(updatedTodo);

        mockMvc.perform(put("/api/v1/sql/todo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Todo"));
    }

    @Test
    @WithMockUser
    void updateTodoNotFoundShouldReturnNotFound() throws Exception {
        TodoRequest request = new TodoRequest("Updated Todo", "Updated Description");

        given(todoService.updateTodo(eq(99L), anyString(), anyString()))
                .willThrow(new IllegalArgumentException("Todo not found with id: 99"));

        mockMvc.perform(put("/api/v1/sql/todo/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void toggleTodoCompletionShouldReturnToggledTodo() throws Exception {
        Todo toggledTodo = new Todo(1L, "Test Todo", "Test Description", true, LocalDateTime.now(), LocalDateTime.now());

        given(todoService.toggleTodoCompletion(1L)).willReturn(toggledTodo);

        mockMvc.perform(patch("/api/v1/sql/todo/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @WithMockUser
    void toggleTodoCompletionNotFoundShouldReturnNotFound() throws Exception {
        given(todoService.toggleTodoCompletion(99L))
                .willThrow(new IllegalArgumentException("Todo not found with id: 99"));

        mockMvc.perform(patch("/api/v1/sql/todo/99/toggle"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteTodoShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/sql/todo/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteTodoNotFoundShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Todo not found with id: 99"))
                .when(todoService).deleteTodo(99L);

        mockMvc.perform(delete("/api/v1/sql/todo/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void postShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/sql/todo/1"))
                .andExpect(status().isMethodNotAllowed());
    }
}
