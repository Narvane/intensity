package com.intensity.participant.dto;

import java.util.UUID;

public record RegisterParticipantResponse(
		UUID id,
		String displayName,
		String email,
		String token) {
}
