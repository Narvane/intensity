package com.intensity;

import com.intensity.experience.service.ExperienceVisibilityPolicy;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperienceVisibilityPolicyTest {

	private final ExperienceVisibilityPolicy policy = new ExperienceVisibilityPolicy();

	@Test
	void authorSeesFullContentInExperiencesMode() {
		UUID authorId = UUID.randomUUID();

		assertTrue(policy.hasFullContent(
				new com.intensity.common.AuthPrincipal(
						authorId, com.intensity.common.AccessMode.EXPERIENCES, null, java.util.List.of()),
				authorId,
				authorId));
	}

	@Test
	void nonAuthorSeesSummaryInExperiencesMode() {
		UUID authorId = UUID.randomUUID();
		UUID viewerId = UUID.randomUUID();

		assertFalse(policy.hasFullContent(
				new com.intensity.common.AuthPrincipal(
						viewerId, com.intensity.common.AccessMode.EXPERIENCES, null, java.util.List.of()),
				authorId,
				viewerId));
	}

	@Test
	void experienceBoxSessionSeesFullContentForDrawReveal() {
		UUID authorId = UUID.randomUUID();
		UUID viewerId = UUID.randomUUID();

		assertTrue(policy.hasFullContent(
				new com.intensity.common.AuthPrincipal(
						viewerId,
						com.intensity.common.AccessMode.EXPERIENCE_BOX,
						UUID.randomUUID(),
						java.util.List.of(viewerId)),
				authorId,
				viewerId));
	}
}
