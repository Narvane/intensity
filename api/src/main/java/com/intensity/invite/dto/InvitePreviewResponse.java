package com.intensity.invite.dto;

import com.intensity.invite.entity.InviteStatus;
import com.intensity.group.dto.GroupMemberResponse;

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
