CREATE TABLE collateral (
    id                    UUID PRIMARY KEY,
    loan_application_id   UUID NOT NULL REFERENCES loan_applications (id) ON DELETE CASCADE,
    type                  VARCHAR(20) NOT NULL,
    description           TEXT NOT NULL,
    estimated_value       NUMERIC(14, 2) NOT NULL,
    registered_by         UUID NOT NULL REFERENCES users (id),
    registered_at         TIMESTAMPTZ NOT NULL,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_collateral_loan_application_id ON collateral (loan_application_id);
