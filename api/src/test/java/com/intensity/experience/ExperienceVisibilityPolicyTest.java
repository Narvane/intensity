package com.intensity.experience;

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
				new com.intensity.platform.common.AuthPrincipal(
						authorId, com.intensity.platform.common.AccessMode.EXPERIENCES, null, java.util.List.of(), java.util.List.of()),
				authorId,
				authorId));
	}

	@Test
	void nonAuthorSeesSummaryInExperiencesMode() {
		UUID authorId = UUID.randomUUID();
		UUID viewerId = UUID.randomUUID();

		assertFalse(policy.hasFullContent(
				new com.intensity.platform.common.AuthPrincipal(
						viewerId, com.intensity.platform.common.AccessMode.EXPERIENCES, null, java.util.List.of(), java.util.List.of()),
				authorId,
				viewerId));
	}

	@Test
	void experienceBoxSessionSeesFullContentForDrawReveal() {
		UUID authorId = UUID.randomUUID();
		UUID viewerId = UUID.randomUUID();

		UUID groupId = UUID.randomUUID();

		assertTrue(policy.hasFullContent(
				new com.intensity.platform.common.AuthPrincipal(
						viewerId,
						com.intensity.platform.common.AccessMode.EXPERIENCE_BOX,
						groupId,
						java.util.List.of(groupId),
						java.util.List.of(viewerId)),
				authorId,
				viewerId));
	}
}
