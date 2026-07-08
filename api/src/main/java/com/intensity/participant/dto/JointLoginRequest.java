package com.intensity.participant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record JointLoginRequest(
		List<@Valid LoginRequest> credentials,
		String reuseSessionToken) {

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
}
