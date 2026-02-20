package com.company.project.application;

import com.company.project.common.exception.ResourceNotFoundException;
import com.company.project.common.service.CrudService;
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
public class TodoService implements CrudService<Todo, String> {

    private final TodoRepository todoRepository;

    @Override
    public Todo create(Todo entity) {
        log.info("Creating new todo with title: {}", entity.getTitle());
        Todo saved = todoRepository.save(entity);
        log.info("Created todo with id: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Todo> findById(String id) {
        log.debug("Fetching todo by id: {}", id);
        return todoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> findAll() {
        log.debug("Fetching all todos");
        return todoRepository.findAll();
    }

    @Override
    public Todo update(String id, Todo entity) {
        log.info("Updating todo with id: {}", id);
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
        Todo updatedTodo = existingTodo.withUpdates(entity.getTitle(), entity.getDescription());
        Todo saved = todoRepository.save(updatedTodo);
        log.info("Updated todo with id: {}", saved.getId());
        return saved;
    }

    @Override
    public void delete(String id) {
        log.info("Deleting todo with id: {}", id);
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Todo", "id", id);
        }
        todoRepository.deleteById(id);
        log.info("Deleted todo with id: {}", id);
    }

    @Override
    public boolean exists(String id) {
        return todoRepository.existsById(id);
    }

    public Todo toggleCompletion(String id) {
        log.info("Toggling completion for todo with id: {}", id);
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
        Todo toggledTodo = existingTodo.withCompletion(!existingTodo.isCompleted());
        Todo saved = todoRepository.save(toggledTodo);
        log.info("Toggled todo with id: {}, completed: {}", saved.getId(), saved.isCompleted());
        return saved;
    }
}
