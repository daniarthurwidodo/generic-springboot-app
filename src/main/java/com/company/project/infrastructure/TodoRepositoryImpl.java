package com.company.project.infrastructure;

import com.company.project.domain.Todo;
import com.company.project.domain.TodoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepositoryImpl implements TodoRepository {

    private final TodoJpaRepository todoJpaRepository;

    public TodoRepositoryImpl(TodoJpaRepository todoJpaRepository) {
        this.todoJpaRepository = todoJpaRepository;
    }

    @Override
    public Todo save(Todo todo) {
        TodoJpaEntity entity;
        if (todo.getId() == null) {
            entity = new TodoJpaEntity(todo.getTitle(), todo.getDescription());
        } else {
            entity = todoJpaRepository.findById(todo.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Todo not found with id: " + todo.getId()));
            entity.setTitle(todo.getTitle());
            entity.setDescription(todo.getDescription());
            entity.setCompleted(todo.isCompleted());
            entity.setUpdatedAt(todo.getUpdatedAt());
        }
        TodoJpaEntity saved = todoJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Todo> findAll() {
        return todoJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        todoJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return todoJpaRepository.existsById(id);
    }

    private Todo toDomain(TodoJpaEntity entity) {
        return new Todo(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.isCompleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
