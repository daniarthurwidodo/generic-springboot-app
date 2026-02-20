package com.company.project.domain;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    Todo save(Todo todo);

    Optional<Todo> findById(String id);

    List<Todo> findAll();

    void deleteById(String id);

    boolean existsById(String id);
}
