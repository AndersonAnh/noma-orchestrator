-- liquibase formatted sql

-- changeset orchestrator:create-client-table
CREATE TABLE client (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    age INTEGER
);
