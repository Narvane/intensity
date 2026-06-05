ALTER TABLE intensity2.experience_boxes
    ADD COLUMN box_type VARCHAR(64) NOT NULL DEFAULT 'saidas_amigos';

COMMENT ON COLUMN intensity2.experience_boxes.box_type IS 'Level 2 contextual type (legacy code, e.g. saidas_amigos).';
