package com.company.project.common.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for abstracting data access
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface GenericRepositoryAdapter<T, ID> {
    
    T save(T entity);
    
    Optional<T> findById(ID id);
    
    List<T> findAll();
    
    void deleteById(ID id);
    
    boolean existsById(ID id);
    
    long count();
}
