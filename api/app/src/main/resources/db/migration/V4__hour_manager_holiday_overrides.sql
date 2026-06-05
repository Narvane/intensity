CREATE TABLE hour_manager.holiday_overrides
(
    id            UUID PRIMARY KEY,
    override_date DATE    NOT NULL,
    is_holiday    BOOLEAN NOT NULL,
    CONSTRAINT uq_hm_holiday_overrides_date UNIQUE (override_date)
);

CREATE INDEX idx_hm_holiday_overrides_date ON hour_manager.holiday_overrides (override_date);
