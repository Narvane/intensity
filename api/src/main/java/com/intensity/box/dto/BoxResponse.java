package com.intensity.box.dto;

import com.intensity.box.entity.BoxType;

import java.time.Instant;
import java.util.UUID;

public record BoxResponse(
		UUID id,
		UUID groupId,
		String name,
		BoxType type,
		Instant createdAt,
		long experienceCount) {
}
