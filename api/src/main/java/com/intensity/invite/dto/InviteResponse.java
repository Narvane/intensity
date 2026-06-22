package com.intensity.invite.dto;

import com.intensity.invite.entity.InviteStatus;

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
