package com.intensity.group.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GroupResponse(
		UUID id, int memberCount, Instant createdAt, List<GroupMemberResponse> members) {
}
