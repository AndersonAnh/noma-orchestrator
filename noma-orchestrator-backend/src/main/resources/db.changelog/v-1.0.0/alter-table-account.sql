-- liquibase formatted sql

-- changeset a.solovev:alter-table-account

ALTER TABLE accounts
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();