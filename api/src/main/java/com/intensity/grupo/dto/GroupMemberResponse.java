package com.intensity.grupo.dto;

import java.util.UUID;

public record GroupMemberResponse(UUID participantId, String displayName) {
}
