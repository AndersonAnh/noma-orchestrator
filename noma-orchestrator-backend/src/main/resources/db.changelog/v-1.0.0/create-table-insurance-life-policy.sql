-- liquibase formatted sql

-- changeset a.solovev:create-insurance_life_policy-table
CREATE TABLE IF NOT EXISTS insurance_life_policy (
    id UUID PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL UNIQUE,
    client_id UUID NOT NULL,
    agent_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    insured_amount NUMERIC(15, 2) NOT NULL CHECK (insured_amount > 0),
    premium_amount NUMERIC(15, 2) NOT NULL CHECK (premium_amount > 0),
    policy_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    coverage_type VARCHAR(20) NOT NULL,
    special_conditions VARCHAR(2000),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL
);

-- changeset a.solovev:add-indexes

CREATE INDEX IF NOT EXISTS idx_insurance_life_client_id ON insurance_life_policy(client_id);
CREATE INDEX IF NOT EXISTS idx_insurance_life_agent_id ON insurance_life_policy(agent_id);