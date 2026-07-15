package com.intensity.platform.common;

import com.intensity.platform.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

public record AuthPrincipal(
		UUID participantId,
		AccessMode accessMode,
		UUID groupId,
		List<UUID> groupIds,
		List<UUID> participantIds) {

	public static AuthPrincipal fromClaims(Claims claims) {
		AccessMode mode = AccessMode.valueOf(claims.get("accessMode", String.class));

		if (mode == AccessMode.EXPERIENCES) {
			return new AuthPrincipal(
					UUID.fromString(claims.getSubject()),
					mode,
					null,
					List.of(),
					List.of());
		}

		@SuppressWarnings("unchecked")
		List<String> rawGroupIds = claims.get("groupIds", List.class);
		List<UUID> groupIds;
		if (rawGroupIds != null && !rawGroupIds.isEmpty()) {
			groupIds = rawGroupIds.stream().map(UUID::fromString).toList();
		} else {
			groupIds = List.of(UUID.fromString(claims.get("groupId", String.class)));
		}

		UUID groupId = groupIds.getFirst();
		@SuppressWarnings("unchecked")
		List<String> rawIds = claims.get("participantIds", List.class);
		List<UUID> participantIds = rawIds.stream().map(UUID::fromString).toList();

		return new AuthPrincipal(participantIds.getFirst(), mode, groupId, groupIds, participantIds);
	}

	public static AuthPrincipal requireCurrent() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal)) {
			throw JwtService.unauthorized();
		}
		return principal;
	}

	public boolean canAccessExperienceBoxGroup(UUID targetGroupId) {
		return accessMode == AccessMode.EXPERIENCE_BOX && groupIds.contains(targetGroupId);
	}
}
