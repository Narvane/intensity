-- Consolidate intensity2 schema into intensity (canonical name after multi-project refactor).
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'intensity2') THEN
        IF EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'intensity') THEN
            DROP SCHEMA intensity CASCADE;
        END IF;
        ALTER SCHEMA intensity2 RENAME TO intensity;
    END IF;
END $$;
