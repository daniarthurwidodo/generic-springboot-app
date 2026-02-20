package com.company.project.common.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD service interface for reusable operations
 * @param <T> Domain entity type
 * @param <ID> ID type
 */
public interface CrudService<T, ID> {
    
    T create(T entity);
    
    Optional<T> findById(ID id);
    
    List<T> findAll();
    
    T update(ID id, T entity);
    
    void delete(ID id);
    
    boolean exists(ID id);
}
