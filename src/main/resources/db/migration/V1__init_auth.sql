CREATE TABLE users (
    id                     UUID PRIMARY KEY,
    email                  VARCHAR(255) NOT NULL,
    password_hash          VARCHAR(255) NOT NULL,
    first_name             VARCHAR(100) NOT NULL,
    last_name              VARCHAR(100) NOT NULL,
    role                   VARCHAR(50)  NOT NULL,
    enabled                BOOLEAN      NOT NULL DEFAULT TRUE,
    failed_login_attempts  INTEGER      NOT NULL DEFAULT 0,
    locked_until           TIMESTAMPTZ,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ
);

CREATE UNIQUE INDEX ux_users_email ON users (email);

CREATE TABLE refresh_tokens (
    id                  UUID PRIMARY KEY,
    user_id             UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash          VARCHAR(64) NOT NULL,
    family_id           UUID NOT NULL,
    replaced_by_token_id UUID,
    expires_at          TIMESTAMPTZ NOT NULL,
    revoked_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_refresh_tokens_token_hash ON refresh_tokens (token_hash);
CREATE INDEX ix_refresh_tokens_user_family ON refresh_tokens (user_id, family_id);
CREATE INDEX ix_refresh_tokens_expires_at ON refresh_tokens (expires_at);

CREATE TABLE password_reset_tokens (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash  VARCHAR(64) NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    used_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ux_password_reset_tokens_token_hash ON password_reset_tokens (token_hash);
