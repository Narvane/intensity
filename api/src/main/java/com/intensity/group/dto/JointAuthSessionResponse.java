package com.intensity.group.dto;

import com.intensity.common.AccessMode;

import java.util.List;
import java.util.UUID;

public record JointAuthSessionResponse(
		String token,
		UUID groupId,
		List<UUID> groupIds,
		List<GroupMemberResponse> members,
		AccessMode accessMode) {
}
