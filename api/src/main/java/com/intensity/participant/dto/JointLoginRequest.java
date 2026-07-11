package com.intensity.participant.dto;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public record JointLoginRequest(
		List<@Valid LoginRequest> credentials,
		String reuseSessionToken,
		UUID targetGroupId,
		Boolean requireAllMembers) {

	public JointLoginRequest {
		if (credentials == null) {
			credentials = List.of();
		}
	}

	public boolean hasReuseSessionToken() {
		return reuseSessionToken != null && !reuseSessionToken.isBlank();
	}

	public boolean hasCredentials() {
		return credentials != null && !credentials.isEmpty();
	}

	public boolean hasTargetGroupId() {
		return targetGroupId != null;
	}

	public boolean requiresAllMembers() {
		return Boolean.TRUE.equals(requireAllMembers);
	}
}
