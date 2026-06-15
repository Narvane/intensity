CREATE TABLE experiencia (
    id UUID PRIMARY KEY,
    caixinha_id UUID NOT NULL REFERENCES caixinha(id) ON DELETE CASCADE,
    participante_id UUID NOT NULL REFERENCES participante(id),
    description VARCHAR(1000) NOT NULL,
    reflection VARCHAR(2000) NOT NULL,
    intensity INT NOT NULL,
    effort INT NOT NULL,
    openness INT NOT NULL,
    novelty INT NOT NULL,
    seal VARCHAR(16) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_experiencia_caixinha ON experiencia(caixinha_id);
CREATE INDEX idx_experiencia_author ON experiencia(participante_id);
