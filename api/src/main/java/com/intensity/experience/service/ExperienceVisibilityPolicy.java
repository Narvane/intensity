package com.intensity.experience.service;

import com.intensity.common.AccessMode;
import com.intensity.common.AuthPrincipal;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExperienceVisibilityPolicy {

	public boolean hasFullContent(AuthPrincipal principal, UUID authorId, UUID viewerId) {
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX) {
			return true;
		}

		return authorId.equals(viewerId);
	}
}
