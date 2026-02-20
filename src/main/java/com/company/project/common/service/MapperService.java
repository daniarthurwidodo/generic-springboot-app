package com.company.project.common.service;

/**
 * Generic mapper interface for converting between domain and DTO objects
 * @param <D> Domain entity type
 * @param <REQ> Request DTO type
 * @param <RES> Response DTO type
 */
public interface MapperService<D, REQ, RES> {
    
    D toEntity(REQ request);
    
    RES toResponse(D entity);
}
