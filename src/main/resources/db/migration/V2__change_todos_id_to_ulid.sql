-- Migration to change todos table ID from BIGSERIAL to VARCHAR for ULID support
-- This will drop existing data

DROP TABLE IF EXISTS todos CASCADE;

CREATE TABLE todos (
    id VARCHAR(26) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_todos_completed ON todos(completed);
CREATE INDEX idx_todos_created_at ON todos(created_at);
