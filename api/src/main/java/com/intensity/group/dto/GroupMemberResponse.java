package com.intensity.group.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GroupMemberResponse(UUID participantId, String displayName, String email) {

	public GroupMemberResponse(UUID participantId, String displayName) {
		this(participantId, displayName, null);
	}
}
