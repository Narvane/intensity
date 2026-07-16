CREATE TABLE password_reset_token (
    id UUID PRIMARY KEY,
    participant_id UUID NOT NULL REFERENCES participant(id) ON DELETE CASCADE,
    token UUID NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP
);

CREATE UNIQUE INDEX idx_password_reset_token_token ON password_reset_token(token);
CREATE INDEX idx_password_reset_token_participant ON password_reset_token(participant_id);
