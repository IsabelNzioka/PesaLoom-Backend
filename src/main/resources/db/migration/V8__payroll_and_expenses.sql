CREATE TABLE payroll_records (
    id                    UUID PRIMARY KEY,
    staff_user_id         UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    period_month          VARCHAR(7) NOT NULL,
    base_salary           NUMERIC(14, 2) NOT NULL,
    deductions            NUMERIC(14, 2) NOT NULL DEFAULT 0,
    net_pay               NUMERIC(14, 2) NOT NULL,
    paid_at               TIMESTAMPTZ NOT NULL,
    recorded_by           UUID NOT NULL REFERENCES users (id),
    notes                 TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_payroll_records_staff_user_id ON payroll_records (staff_user_id);
CREATE INDEX ix_payroll_records_period_month ON payroll_records (period_month);

CREATE TABLE expenses (
    id                    UUID PRIMARY KEY,
    category              VARCHAR(50) NOT NULL,
    description           TEXT NOT NULL,
    amount                NUMERIC(14, 2) NOT NULL,
    incurred_at           TIMESTAMPTZ NOT NULL,
    recorded_by           UUID NOT NULL REFERENCES users (id),
    notes                 TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_expenses_incurred_at ON expenses (incurred_at);
CREATE INDEX ix_expenses_category ON expenses (category);
