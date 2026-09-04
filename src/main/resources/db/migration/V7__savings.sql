CREATE TABLE savings_accounts (
    id                    UUID PRIMARY KEY,
    user_id               UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    account_number        VARCHAR(20) NOT NULL,
    balance               NUMERIC(14, 2) NOT NULL DEFAULT 0,
    interest_rate         NUMERIC(5, 2) NOT NULL DEFAULT 0,
    status                VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    opened_at             TIMESTAMPTZ NOT NULL,
    closed_at             TIMESTAMPTZ,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_savings_accounts_account_number ON savings_accounts (account_number);
CREATE INDEX ix_savings_accounts_user_id ON savings_accounts (user_id);
CREATE INDEX ix_savings_accounts_status ON savings_accounts (status);

CREATE TABLE savings_transactions (
    id                    UUID PRIMARY KEY,
    account_id            UUID NOT NULL REFERENCES savings_accounts (id) ON DELETE CASCADE,
    type                  VARCHAR(20) NOT NULL,
    amount                NUMERIC(14, 2) NOT NULL,
    balance_after         NUMERIC(14, 2) NOT NULL,
    recorded_by           UUID NOT NULL REFERENCES users (id),
    notes                 TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_savings_transactions_account_id ON savings_transactions (account_id);
