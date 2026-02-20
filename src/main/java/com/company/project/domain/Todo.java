package com.company.project.domain;

import java.time.LocalDateTime;

public class Todo {

    private final Long id;
    private final String title;
    private final String description;
    private final boolean completed;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Todo(Long id, String title, String description, boolean completed, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Todo(String title, String description) {
        this(null, title, description, false, LocalDateTime.now(), LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Todo withCompletion(boolean completed) {
        return new Todo(this.id, this.title, this.description, completed, this.createdAt, LocalDateTime.now());
    }

    public Todo withUpdates(String title, String description) {
        return new Todo(this.id, title, description, this.completed, this.createdAt, LocalDateTime.now());
    }
}
