CREATE TABLE loan_applications (
    id                     UUID PRIMARY KEY,
    user_id                UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status                 VARCHAR(20)  NOT NULL,
    loan_type              VARCHAR(20),
    loan_amount            NUMERIC(14, 2),
    loan_tenure_months     INTEGER,
    loan_purpose           VARCHAR(100),
    referral_code          VARCHAR(20),
    reference_number       VARCHAR(20),
    current_step           INTEGER      NOT NULL DEFAULT 1,

    kra_pin                VARCHAR(11),
    kra_pin_verified       BOOLEAN      NOT NULL DEFAULT FALSE,
    kra_pin_verified_at    TIMESTAMPTZ,
    national_id            VARCHAR(8),
    national_id_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    national_id_verified_at TIMESTAMPTZ,
    id_consent             BOOLEAN      NOT NULL DEFAULT FALSE,

    personal_info          JSONB,
    address                JSONB,
    employment             JSONB,
    co_applicant           JSONB,
    disbursement           JSONB,
    consents               JSONB,

    emi                    NUMERIC(14, 2),
    interest_rate          NUMERIC(5, 2),
    processing_fee         NUMERIC(14, 2),
    total_interest         NUMERIC(14, 2),
    total_payable          NUMERIC(14, 2),

    submitted_at           TIMESTAMPTZ,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ
);

CREATE INDEX ix_loan_applications_user_id ON loan_applications (user_id);
CREATE INDEX ix_loan_applications_status ON loan_applications (status);

-- One active draft per user at a time — the wizard only ever has one loan type mounted,
-- so loan_type is just a mutable column on that single draft row, not part of the key.
CREATE UNIQUE INDEX ux_loan_applications_one_draft_per_user ON loan_applications (user_id) WHERE status = 'DRAFT';

CREATE UNIQUE INDEX ux_loan_applications_reference_number ON loan_applications (reference_number) WHERE reference_number IS NOT NULL;

CREATE TABLE documents (
    id                    UUID PRIMARY KEY,
    loan_application_id   UUID NOT NULL REFERENCES loan_applications (id) ON DELETE CASCADE,
    document_key          VARCHAR(50) NOT NULL,
    original_filename     VARCHAR(255) NOT NULL,
    content_type          VARCHAR(100) NOT NULL,
    size_bytes            BIGINT NOT NULL,
    storage_path          VARCHAR(500) NOT NULL,
    uploaded_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_documents_loan_application_id ON documents (loan_application_id);

CREATE TABLE postal_codes (
    code    VARCHAR(5) PRIMARY KEY,
    town    VARCHAR(100) NOT NULL,
    county  VARCHAR(50) NOT NULL
);

INSERT INTO postal_codes (code, town, county) VALUES
    ('00100', 'Nairobi GPO', 'Nairobi'),
    ('00200', 'Nairobi City Square', 'Nairobi'),
    ('00500', 'Nairobi Industrial Area', 'Nairobi'),
    ('00600', 'Westlands', 'Nairobi'),
    ('00800', 'Nairobi West', 'Nairobi'),
    ('01000', 'Thika', 'Kiambu'),
    ('80100', 'Mombasa', 'Mombasa'),
    ('80200', 'Malindi', 'Kilifi'),
    ('80300', 'Voi', 'Taita-Taveta'),
    ('40100', 'Kisumu', 'Kisumu'),
    ('40200', 'Kisii', 'Kisii'),
    ('20100', 'Nakuru', 'Nakuru'),
    ('20117', 'Naivasha', 'Nakuru'),
    ('20200', 'Kericho', 'Kericho'),
    ('30100', 'Eldoret', 'Uasin Gishu'),
    ('30200', 'Kitale', 'Trans Nzoia'),
    ('50100', 'Kakamega', 'Kakamega'),
    ('60100', 'Embu', 'Embu'),
    ('60200', 'Meru', 'Meru'),
    ('90100', 'Machakos', 'Machakos'),
    ('90200', 'Kitui', 'Kitui'),
    ('10100', 'Nyeri', 'Nyeri'),
    ('70100', 'Garissa', 'Garissa');
