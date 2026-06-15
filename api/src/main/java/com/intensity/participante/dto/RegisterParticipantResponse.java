package com.intensity.participante.dto;

import java.util.UUID;

public record RegisterParticipantResponse(
		UUID id,
		String displayName,
		String email,
		String token) {
}
