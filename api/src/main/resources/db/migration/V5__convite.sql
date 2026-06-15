CREATE TABLE convite (
    id UUID PRIMARY KEY,
    grupo_id UUID NOT NULL REFERENCES grupo(id) ON DELETE CASCADE,
    creator_id UUID NOT NULL REFERENCES participante(id),
    acceptor_id UUID REFERENCES participante(id),
    code VARCHAR(6) NOT NULL,
    link_token UUID NOT NULL,
    status VARCHAR(16) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP
);

CREATE UNIQUE INDEX idx_convite_code ON convite(code);
CREATE UNIQUE INDEX idx_convite_link_token ON convite(link_token);
CREATE INDEX idx_convite_grupo ON convite(grupo_id);
CREATE INDEX idx_convite_grupo_status ON convite(grupo_id, status);
