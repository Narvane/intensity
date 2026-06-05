ALTER TABLE intensity2.experience_boxes
    ALTER COLUMN box_type SET DEFAULT 'outings_friends';

UPDATE intensity2.experience_boxes
SET box_type = CASE lower(box_type)
    WHEN 'saidas_amigos' THEN 'outings_friends'
    WHEN 'saidas_casal' THEN 'outings_couple'
    WHEN 'viagens_amigos' THEN 'trips_friends'
    WHEN 'viagens_casal' THEN 'trips_couple'
    WHEN 'intimos_casal' THEN 'intimate_couple'
    WHEN 'experiencias_amigos' THEN 'experiences_friends'
    WHEN 'experiências_amigos' THEN 'experiences_friends'
    WHEN 'sair_rotina' THEN 'break_routine'
    WHEN 'primeiras_vezes' THEN 'first_times'
    WHEN 'desconforto_leve' THEN 'light_discomfort'
    WHEN 'momentos_conexao' THEN 'connection_moments'
    WHEN 'momentos_conexão' THEN 'connection_moments'
    WHEN 'experiencias_diferentes' THEN 'different_experiences'
    WHEN 'experiências_diferentes' THEN 'different_experiences'
    ELSE box_type
END;

COMMENT ON COLUMN intensity2.experience_boxes.box_type IS 'Level 2 contextual type (English codes, e.g. outings_friends).';
