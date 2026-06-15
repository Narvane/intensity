package com.intensity.convite.dto;

import com.intensity.convite.entity.InviteStatus;

import java.time.Instant;
import java.util.UUID;

public record InviteResponse(
		UUID id,
		UUID groupId,
		String code,
		UUID linkToken,
		Instant expiresAt,
		InviteStatus status,
		Instant createdAt) {
}
