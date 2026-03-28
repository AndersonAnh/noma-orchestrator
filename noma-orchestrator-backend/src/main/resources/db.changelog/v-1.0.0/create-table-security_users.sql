-- liquibase formatted sql

-- changeset a.solovev orchestrator:create-security_users
CREATE TABLE security_users (
                                id BIGSERIAL PRIMARY KEY,
                                first_name VARCHAR(255),
                                last_name VARCHAR(255),
                                email VARCHAR(255) NOT NULL UNIQUE,
                                password VARCHAR(255) NOT NULL,
                                role VARCHAR(50) NOT NULL
);