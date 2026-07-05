-- Reconceptualize experience parameters and add experience type.

-- Openness becomes Unpredictability (a fully different concept in the product).
ALTER TABLE experience RENAME COLUMN openness TO unpredictability;

-- Experience type shown on the draw card cover; defaults to NONE ("Sem tipo").
ALTER TABLE experience ADD COLUMN type VARCHAR(32) NOT NULL DEFAULT 'NONE';

-- Reflection ("Why is this experience interesting?") is now optional.
ALTER TABLE experience ALTER COLUMN reflection DROP NOT NULL;
