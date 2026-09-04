CREATE TABLE loans (
    id                     UUID PRIMARY KEY,
    loan_application_id    UUID NOT NULL REFERENCES loan_applications (id) ON DELETE CASCADE,
    user_id                UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    reference_number       VARCHAR(20) NOT NULL,
    loan_type              VARCHAR(20) NOT NULL,
    principal              NUMERIC(14, 2) NOT NULL,
    interest_rate          NUMERIC(5, 2) NOT NULL,
    tenure_months           INTEGER NOT NULL,
    emi                    NUMERIC(14, 2) NOT NULL,
    total_payable          NUMERIC(14, 2) NOT NULL,
    status                 VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    disbursed_at           TIMESTAMPTZ NOT NULL,
    closed_at              TIMESTAMPTZ,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_loans_loan_application_id ON loans (loan_application_id);
CREATE INDEX ix_loans_user_id ON loans (user_id);
CREATE INDEX ix_loans_status ON loans (status);

CREATE TABLE loan_installments (
    id                    UUID PRIMARY KEY,
    loan_id               UUID NOT NULL REFERENCES loans (id) ON DELETE CASCADE,
    installment_number    INTEGER NOT NULL,
    due_date              DATE NOT NULL,
    principal_due         NUMERIC(14, 2) NOT NULL,
    interest_due          NUMERIC(14, 2) NOT NULL,
    total_due             NUMERIC(14, 2) NOT NULL,
    amount_paid           NUMERIC(14, 2) NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX ux_loan_installments_loan_id_number ON loan_installments (loan_id, installment_number);
CREATE INDEX ix_loan_installments_loan_id ON loan_installments (loan_id);
CREATE INDEX ix_loan_installments_due_date ON loan_installments (due_date);

CREATE TABLE loan_repayments (
    id                    UUID PRIMARY KEY,
    loan_id               UUID NOT NULL REFERENCES loans (id) ON DELETE CASCADE,
    amount                NUMERIC(14, 2) NOT NULL,
    method                VARCHAR(20) NOT NULL,
    paid_at               TIMESTAMPTZ NOT NULL,
    recorded_by           UUID NOT NULL REFERENCES users (id),
    notes                 TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_loan_repayments_loan_id ON loan_repayments (loan_id);
