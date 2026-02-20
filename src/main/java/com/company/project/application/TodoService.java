package com.company.project.application;

import com.company.project.domain.Todo;
import com.company.project.domain.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public Todo createTodo(String title, String description) {
        log.info("Creating new todo with title: {}", title);
        Todo todo = new Todo(title, description);
        Todo saved = todoRepository.save(todo);
        log.info("Created todo with id: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Todo> getTodoById(Long id) {
        log.debug("Fetching todo by id: {}", id);
        return todoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Todo> getAllTodos() {
        log.debug("Fetching all todos");
        return todoRepository.findAll();
    }

    public Todo updateTodo(Long id, String title, String description) {
        log.info("Updating todo with id: {}", id);
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found with id: " + id));
        Todo updatedTodo = existingTodo.withUpdates(title, description);
        Todo saved = todoRepository.save(updatedTodo);
        log.info("Updated todo with id: {}", saved.getId());
        return saved;
    }

    public Todo toggleTodoCompletion(Long id) {
        log.info("Toggling completion for todo with id: {}", id);
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found with id: " + id));
        Todo toggledTodo = existingTodo.withCompletion(!existingTodo.isCompleted());
        Todo saved = todoRepository.save(toggledTodo);
        log.info("Toggled todo with id: {}, completed: {}", saved.getId(), saved.isCompleted());
        return saved;
    }

    public void deleteTodo(Long id) {
        log.info("Deleting todo with id: {}", id);
        if (!todoRepository.existsById(id)) {
            throw new IllegalArgumentException("Todo not found with id: " + id);
        }
        todoRepository.deleteById(id);
        log.info("Deleted todo with id: {}", id);
    }
}
