package com.intensity.group.dto;

import java.util.UUID;

public record GroupMemberResponse(UUID participantId, String displayName) {
}
