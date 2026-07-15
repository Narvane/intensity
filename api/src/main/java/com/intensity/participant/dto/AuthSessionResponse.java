package com.intensity.participant.dto;

import com.intensity.platform.common.AccessMode;

import java.util.UUID;

public record AuthSessionResponse(
		String token,
		UUID participantId,
		String displayName,
		AccessMode accessMode) {
}
