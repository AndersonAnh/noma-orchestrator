-- liquibase formatted sql
-- changeset a.solovev:add-version-to-tables
ALTER TABLE users ADD COLUMN version INT DEFAULT 0;
ALTER TABLE accounts ADD COLUMN version INT DEFAULT 0;
ALTER TABLE transactions ADD COLUMN version INT DEFAULT 0;
ALTER TABLE payment_orders ADD COLUMN version INT DEFAULT 0;