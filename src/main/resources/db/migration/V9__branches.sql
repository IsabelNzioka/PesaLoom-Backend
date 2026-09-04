CREATE TABLE branches (
    id            UUID PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    code          VARCHAR(20) NOT NULL,
    address       VARCHAR(255),
    phone         VARCHAR(20),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_branches_code ON branches (code);

INSERT INTO branches (id, name, code, address, phone)
VALUES ('00000000-0000-0000-0000-000000000001', 'Main Branch', 'MAIN', NULL, NULL);

ALTER TABLE loan_applications ADD COLUMN branch_id UUID REFERENCES branches (id);
ALTER TABLE loans ADD COLUMN branch_id UUID REFERENCES branches (id);
ALTER TABLE users ADD COLUMN branch_id UUID REFERENCES branches (id);

UPDATE loan_applications SET branch_id = '00000000-0000-0000-0000-000000000001' WHERE branch_id IS NULL;
UPDATE loans SET branch_id = '00000000-0000-0000-0000-000000000001' WHERE branch_id IS NULL;
UPDATE users SET branch_id = '00000000-0000-0000-0000-000000000001' WHERE role = 'ADMIN' AND branch_id IS NULL;

CREATE INDEX ix_loan_applications_branch_id ON loan_applications (branch_id);
CREATE INDEX ix_loans_branch_id ON loans (branch_id);
CREATE INDEX ix_users_branch_id ON users (branch_id);
