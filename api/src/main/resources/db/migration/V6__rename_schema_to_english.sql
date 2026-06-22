-- Rename Portuguese domain tables/columns to English (Phase C).

ALTER TABLE grupo_participante RENAME COLUMN grupo_id TO group_id;
ALTER TABLE grupo_participante RENAME COLUMN participante_id TO participant_id;
ALTER TABLE grupo_participante RENAME TO group_participant;

ALTER TABLE participante RENAME TO participant;

ALTER TABLE grupo RENAME TO "group";

ALTER TABLE caixinha RENAME COLUMN grupo_id TO group_id;
ALTER TABLE caixinha RENAME TO box;
ALTER INDEX idx_caixinha_grupo RENAME TO idx_box_group;

ALTER TABLE experiencia RENAME COLUMN caixinha_id TO box_id;
ALTER TABLE experiencia RENAME COLUMN participante_id TO participant_id;
ALTER TABLE experiencia RENAME TO experience;
ALTER INDEX idx_experiencia_caixinha RENAME TO idx_experience_box;
ALTER INDEX idx_experiencia_author RENAME TO idx_experience_author;

ALTER TABLE convite RENAME COLUMN grupo_id TO group_id;
ALTER TABLE convite RENAME TO invite;
ALTER INDEX idx_convite_code RENAME TO idx_invite_code;
ALTER INDEX idx_convite_link_token RENAME TO idx_invite_link_token;
ALTER INDEX idx_convite_grupo RENAME TO idx_invite_group;
ALTER INDEX idx_convite_grupo_status RENAME TO idx_invite_group_status;

ALTER INDEX idx_grupo_participante_participante RENAME TO idx_group_participant_participant;
