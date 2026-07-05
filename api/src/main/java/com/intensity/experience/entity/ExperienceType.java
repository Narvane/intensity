package com.intensity.experience.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum ExperienceType {
	NONE,
	EXPLORE,
	RANDOMNESS,
	EXPOSURE,
	CONSTRAINTS,
	OVERCOMING,
	CREATIVITY,
	CONTRAST,
	CONNECTION,
	CONTEMPLATION,
	NARRATIVE;

	@JsonValue
	public String toJson() {
		return name().toLowerCase(Locale.ROOT);
	}

	@JsonCreator
	public static ExperienceType fromJson(String value) {
		if (value == null || value.isBlank()) {
			return NONE;
		}
		return ExperienceType.valueOf(value.trim().toUpperCase(Locale.ROOT));
	}
}
