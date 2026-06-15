CREATE TABLE allowlist_email (
    email VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE participante (
    id UUID PRIMARY KEY,
    display_name VARCHAR(80) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grupo (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grupo_participante (
    grupo_id UUID NOT NULL REFERENCES grupo(id) ON DELETE CASCADE,
    participante_id UUID NOT NULL REFERENCES participante(id) ON DELETE CASCADE,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (grupo_id, participante_id)
);

CREATE INDEX idx_grupo_participante_participante ON grupo_participante(participante_id);

INSERT INTO allowlist_email (email) VALUES
    ('alice@example.com'),
    ('bob@example.com'),
    ('carol@example.com');
