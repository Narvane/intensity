package com.intensity.experience.dto;

import com.intensity.experience.entity.ExperienceType;

import java.time.Instant;
import java.util.UUID;

public record ExperienceResponse(
		UUID id,
		UUID boxId,
		UUID authorId,
		String authorDisplayName,
		String description,
		String reflection,
		int intensity,
		ExperienceParametersDto parameters,
		ExperienceType type,
		String seal,
		boolean summaryOnly,
		Instant createdAt,
		Instant updatedAt) {
}
