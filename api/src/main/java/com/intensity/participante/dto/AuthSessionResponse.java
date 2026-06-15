package com.intensity.participante.dto;

import com.intensity.common.AccessMode;

import java.util.UUID;

public record AuthSessionResponse(
		String token,
		UUID participantId,
		String displayName,
		AccessMode accessMode) {
}
