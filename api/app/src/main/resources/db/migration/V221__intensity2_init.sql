CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS intensity2;

CREATE TABLE intensity2.allowed_emails (
    email VARCHAR(255) PRIMARY KEY
);

CREATE TABLE intensity2.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE REFERENCES intensity2.allowed_emails(email),
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE intensity2.groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fingerprint VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE intensity2.group_members (
    group_id UUID NOT NULL REFERENCES intensity2.groups(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES intensity2.users(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, user_id)
);

CREATE TABLE intensity2.experience_boxes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL REFERENCES intensity2.groups(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE intensity2.experiences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description_cipher TEXT NOT NULL,
    description_md5 VARCHAR(32) NOT NULL,
    intensity INTEGER NOT NULL CHECK (intensity IN (1, 2, 3, 4, 5)),
    created_by UUID NOT NULL REFERENCES intensity2.users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    box_id UUID NOT NULL REFERENCES intensity2.experience_boxes(id),
    effort_stars INTEGER CHECK (effort_stars IS NULL OR (effort_stars >= 1 AND effort_stars <= 5)),
    openness_stars INTEGER CHECK (openness_stars IS NULL OR (openness_stars >= 1 AND openness_stars <= 5)),
    novelty_stars INTEGER CHECK (novelty_stars IS NULL OR (novelty_stars >= 1 AND novelty_stars <= 5)),
    additional_info_cipher TEXT
);

CREATE INDEX idx_intensity2_group_members_user ON intensity2.group_members(user_id);
CREATE INDEX idx_intensity2_experience_boxes_group ON intensity2.experience_boxes(group_id);
CREATE INDEX idx_intensity2_experiences_created_by ON intensity2.experiences(created_by);
CREATE INDEX idx_intensity2_experiences_intensity ON intensity2.experiences(intensity);
CREATE INDEX idx_intensity2_experiences_box ON intensity2.experiences(box_id);
