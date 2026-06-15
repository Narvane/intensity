package com.intensity.convite.dto;

import com.intensity.convite.entity.InviteStatus;
import com.intensity.grupo.dto.GroupMemberResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InvitePreviewResponse(
		UUID inviteId,
		UUID groupId,
		List<GroupMemberResponse> members,
		Instant expiresAt,
		InviteStatus status) {
}
