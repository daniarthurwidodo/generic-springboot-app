package com.company.project.common.controller;

import com.company.project.common.service.CrudService;
import com.company.project.common.service.MapperService;
import com.company.project.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Base controller providing common CRUD operations
 * @param <D> Domain entity type
 * @param <REQ> Request DTO type
 * @param <RES> Response DTO type
 * @param <ID> ID type
 */
@Slf4j
public abstract class BaseController<D, REQ, RES, ID> {
    
    protected final CrudService<D, ID> service;
    protected final MapperService<D, REQ, RES> mapper;
    protected final String resourceName;
    
    protected BaseController(CrudService<D, ID> service, MapperService<D, REQ, RES> mapper, String resourceName) {
        this.service = service;
        this.mapper = mapper;
        this.resourceName = resourceName;
    }
    
    protected ResponseEntity<RES> create(REQ request) {
        log.info("Creating new {}", resourceName);
        D entity = mapper.toEntity(request);
        D created = service.create(entity);
        URI location = buildLocationUri(created);
        log.info("Created {} at location: {}", resourceName, location);
        return ResponseEntity.created(location).body(mapper.toResponse(created));
    }
    
    protected ResponseEntity<List<RES>> findAll() {
        log.debug("Fetching all {}", resourceName);
        List<D> entities = service.findAll();
        List<RES> responses = entities.stream().map(mapper::toResponse).toList();
        log.debug("Returning {} {}", responses.size(), resourceName);
        return ResponseEntity.ok(responses);
    }
    
    protected ResponseEntity<RES> findById(ID id) {
        log.debug("Fetching {} by id: {}", resourceName, id);
        D entity = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName, "id", id));
        return ResponseEntity.ok(mapper.toResponse(entity));
    }
    
    protected ResponseEntity<RES> update(ID id, REQ request) {
        log.info("Updating {} with id: {}", resourceName, id);
        D entity = mapper.toEntity(request);
        D updated = service.update(id, entity);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }
    
    protected ResponseEntity<Void> delete(ID id) {
        log.info("Deleting {} with id: {}", resourceName, id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    protected abstract Object getEntityId(D entity);
    
    private URI buildLocationUri(D entity) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(getEntityId(entity))
                .toUri();
    }
}
