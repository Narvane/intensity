package com.intensity.experiencia.dto;

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
		String seal,
		boolean summaryOnly,
		Instant createdAt,
		Instant updatedAt) {
}
