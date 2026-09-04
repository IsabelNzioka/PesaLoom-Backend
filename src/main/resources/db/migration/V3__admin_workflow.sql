
ALTER TABLE loan_applications
    ADD COLUMN reviewed_by      UUID REFERENCES users (id),
    ADD COLUMN reviewed_at      TIMESTAMPTZ,
    ADD COLUMN review_notes     TEXT,
    ADD COLUMN disbursed_at     TIMESTAMPTZ,
    ADD COLUMN assigned_to      UUID REFERENCES users (id),
    ADD COLUMN created_by_staff BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN description      TEXT,
    ADD COLUMN staff_access     JSONB;

CREATE INDEX ix_loan_applications_reviewed_by ON loan_applications (reviewed_by);
CREATE INDEX ix_loan_applications_assigned_to ON loan_applications (assigned_to);
