-- liquibase formatted sql

-- changeset a.solovev:create-users-table
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       tax_id VARCHAR(20) UNIQUE NOT NULL,
                       phone VARCHAR(15) NOT NULL,
                       email VARCHAR(50) UNIQUE NOT NULL,
                       registration_date TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- changeset a.solovev:create-accounts-table
CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          user_id UUID NOT NULL,
                          balance DECIMAL(15, 2) DEFAULT 0.00,
                          currency VARCHAR(3) NOT NULL CHECK (currency IN ('RUB', 'USD', 'EUR')),
                          status VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED')),
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                          FOREIGN KEY (user_id) REFERENCES users(id)
);

-- changeset a.solovev:create-transactions-table
CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              sender_account_id UUID NOT NULL,
                              receiver_account_id UUID NOT NULL,
                              amount DECIMAL(15, 2) NOT NULL,
                              currency VARCHAR(3) NOT NULL,
                              status VARCHAR(10) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
                              timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                              description TEXT,
                              FOREIGN KEY (sender_account_id) REFERENCES accounts(id),
                              FOREIGN KEY (receiver_account_id) REFERENCES accounts(id)
);

-- changeset a.solovev:create-payment-orders-table
CREATE TABLE payment_orders (
                                id UUID PRIMARY KEY,
                                transaction_id UUID NOT NULL,
                                initiator_user_id UUID NOT NULL,
                                confirmation_code VARCHAR(6) NOT NULL,
                                is_confirmed BOOLEAN DEFAULT FALSE,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                                expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                FOREIGN KEY (transaction_id) REFERENCES transactions(id),
                                FOREIGN KEY (initiator_user_id) REFERENCES users(id)
);

-- changeset a.solovev:add-indexes
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_transactions_sender ON transactions(sender_account_id);
CREATE INDEX idx_transactions_receiver ON transactions(receiver_account_id);