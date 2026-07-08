package com.intensity.group;

import com.intensity.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.Locale;
import java.util.Set;

public final class GroupColor {

	public static final String DEFAULT = "coral";

	private static final Set<String> ALLOWED = Set.of("coral", "teal", "purple", "yellow");

	private GroupColor() {
	}

	public static String normalize(String raw) {
		if (raw == null || raw.isBlank()) {
			return DEFAULT;
		}

		String normalized = raw.trim().toLowerCase(Locale.ROOT);
		if (!ALLOWED.contains(normalized)) {
			throw new ApiException(
					HttpStatus.BAD_REQUEST, "INVALID_GROUP_COLOR", "Group color is not supported.");
		}
		return normalized;
	}
}
