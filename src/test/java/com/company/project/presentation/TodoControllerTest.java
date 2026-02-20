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
        Todo savedTodo = new Todo("01HQZX3Y9F8G7JTRQWKNXVP123", "Test Todo", "Test Description", false, LocalDateTime.now(), LocalDateTime.now());

        given(todoService.createTodo(anyString(), anyString())).willReturn(savedTodo);

        mockMvc.perform(post("/api/v1/sql/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("01HQZX3Y9F8G7JTRQWKNXVP123"))
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
                new Todo("01HQZX3Y9F8G7JTRQWKNXVP123", "Todo 1", "Description 1", false, LocalDateTime.now(), LocalDateTime.now()),
                new Todo("01HQZX3Y9F8G7JTRQWKNXVP456", "Todo 2", "Description 2", true, LocalDateTime.now(), LocalDateTime.now())
        );

        given(todoService.getAllTodos()).willReturn(todos);

        mockMvc.perform(get("/api/v1/sql/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("01HQZX3Y9F8G7JTRQWKNXVP123"))
                .andExpect(jsonPath("$[0].title").value("Todo 1"))
                .andExpect(jsonPath("$[1].id").value("01HQZX3Y9F8G7JTRQWKNXVP456"))
                .andExpect(jsonPath("$[1].title").value("Todo 2"));
    }

    @Test
    @WithMockUser
    void getTodoByIdShouldReturnTodo() throws Exception {
        Todo todo = new Todo("01HQZX3Y9F8G7JTRQWKNXVP123", "Test Todo", "Test Description", false, LocalDateTime.now(), LocalDateTime.now());

        given(todoService.getTodoById("01HQZX3Y9F8G7JTRQWKNXVP123")).willReturn(Optional.of(todo));

        mockMvc.perform(get("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("01HQZX3Y9F8G7JTRQWKNXVP123"))
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    @WithMockUser
    void getTodoByIdNotFoundShouldReturnNotFound() throws Exception {
        given(todoService.getTodoById("01HQZX3Y9F8G7JTRQWKNXVP999")).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateTodoShouldReturnUpdatedTodo() throws Exception {
        TodoRequest request = new TodoRequest("Updated Todo", "Updated Description");
        Todo updatedTodo = new Todo("01HQZX3Y9F8G7JTRQWKNXVP123", "Updated Todo", "Updated Description", false, LocalDateTime.now(), LocalDateTime.now());

        given(todoService.updateTodo(eq("01HQZX3Y9F8G7JTRQWKNXVP123"), anyString(), anyString())).willReturn(updatedTodo);

        mockMvc.perform(put("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("01HQZX3Y9F8G7JTRQWKNXVP123"))
                .andExpect(jsonPath("$.title").value("Updated Todo"));
    }

    @Test
    @WithMockUser
    void updateTodoNotFoundShouldReturnNotFound() throws Exception {
        TodoRequest request = new TodoRequest("Updated Todo", "Updated Description");

        given(todoService.updateTodo(eq("01HQZX3Y9F8G7JTRQWKNXVP999"), anyString(), anyString()))
                .willThrow(new IllegalArgumentException("Todo not found with id: 01HQZX3Y9F8G7JTRQWKNXVP999"));

        mockMvc.perform(put("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void toggleTodoCompletionShouldReturnToggledTodo() throws Exception {
        Todo toggledTodo = new Todo("01HQZX3Y9F8G7JTRQWKNXVP123", "Test Todo", "Test Description", true, LocalDateTime.now(), LocalDateTime.now());

        given(todoService.toggleTodoCompletion("01HQZX3Y9F8G7JTRQWKNXVP123")).willReturn(toggledTodo);

        mockMvc.perform(patch("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP123/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("01HQZX3Y9F8G7JTRQWKNXVP123"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @WithMockUser
    void toggleTodoCompletionNotFoundShouldReturnNotFound() throws Exception {
        given(todoService.toggleTodoCompletion("01HQZX3Y9F8G7JTRQWKNXVP999"))
                .willThrow(new IllegalArgumentException("Todo not found with id: 01HQZX3Y9F8G7JTRQWKNXVP999"));

        mockMvc.perform(patch("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP999/toggle"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteTodoShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP123"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteTodoNotFoundShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Todo not found with id: 01HQZX3Y9F8G7JTRQWKNXVP999"))
                .when(todoService).deleteTodo("01HQZX3Y9F8G7JTRQWKNXVP999");

        mockMvc.perform(delete("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void postShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/v1/sql/todo/01HQZX3Y9F8G7JTRQWKNXVP123"))
                .andExpect(status().isMethodNotAllowed());
    }
}
