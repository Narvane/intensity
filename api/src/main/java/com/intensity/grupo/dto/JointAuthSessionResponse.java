package com.intensity.grupo.dto;

import com.intensity.common.AccessMode;

import java.util.List;
import java.util.UUID;

public record JointAuthSessionResponse(
		String token,
		UUID groupId,
		List<GroupMemberResponse> members,
		AccessMode accessMode) {
}
