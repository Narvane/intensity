package com.intensity;

import com.intensity.invite.service.InviteExpirationPolicy;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InviteExpirationPolicyTest {

	private final InviteExpirationPolicy policy = new InviteExpirationPolicy();

	@Test
	void inviteExpiresSevenDaysAfterCreation() {
		Instant createdAt = Instant.parse("2026-06-15T12:00:00Z");

		assertEquals(
				Instant.parse("2026-06-22T12:00:00Z"),
				policy.expiresAt(createdAt));
	}
}
