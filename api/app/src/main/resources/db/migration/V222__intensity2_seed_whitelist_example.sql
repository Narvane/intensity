INSERT INTO intensity2.allowed_emails (email)
VALUES
    ('proponente@intensity.app'),
    ('membro1@intensity.app'),
    ('membro2@intensity.app')
ON CONFLICT (email) DO NOTHING;
