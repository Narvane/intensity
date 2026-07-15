package com.intensity.invite;

import com.intensity.invite.entity.Invite;
import com.intensity.invite.entity.InviteStatus;
import com.intensity.group.entity.Group;
import com.intensity.participant.entity.Participant;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InviteTest {

	@Test
	void activeInviteExpiresAfterValidityWindow() {
		Invite invite = sampleInvite(
				Instant.parse("2026-06-15T12:00:00Z"),
				Instant.parse("2026-06-22T12:00:00Z"));

		assertEquals(
				InviteStatus.EXPIRED,
				invite.effectiveStatus(Instant.parse("2026-06-22T12:00:01Z")));
		assertFalse(invite.canBeAccepted(Instant.parse("2026-06-22T12:00:01Z")));
	}

	@Test
	void activeInviteCanBeAcceptedBeforeExpiry() {
		Invite invite = sampleInvite(
				Instant.parse("2026-06-15T12:00:00Z"),
				Instant.parse("2026-06-22T12:00:00Z"));

		assertTrue(invite.canBeAccepted(Instant.parse("2026-06-21T12:00:00Z")));
	}

	@Test
	void acceptedInviteRecordsAcceptor() {
		Invite invite = sampleInvite(
				Instant.parse("2026-06-15T12:00:00Z"),
				Instant.parse("2026-06-22T12:00:00Z"));
		Participant acceptor = new Participant("Bob", "bob@example.com", "hash");
		Instant acceptedAt = Instant.parse("2026-06-16T10:00:00Z");

		invite.markAccepted(acceptor, acceptedAt);

		assertEquals(InviteStatus.ACCEPTED, invite.getStatus());
		assertEquals(acceptor, invite.getAcceptor());
		assertEquals(acceptedAt, invite.getAcceptedAt());
	}

	@Test
	void revokedInviteCannotBeAccepted() {
		Invite invite = sampleInvite(
				Instant.parse("2026-06-15T12:00:00Z"),
				Instant.parse("2026-06-22T12:00:00Z"));
		invite.markRevoked();

		assertThrows(IllegalStateException.class, () -> invite.markAccepted(
				new Participant("Bob", "bob@example.com", "hash"),
				Instant.parse("2026-06-16T10:00:00Z")));
	}

	private Invite sampleInvite(Instant createdAt, Instant expiresAt) {
		Group group = Group.createNew();
		Participant creator = new Participant("Alice", "alice@example.com", "hash");
		return new Invite(group, creator, "AB23CD", UUID.randomUUID(), expiresAt, createdAt);
	}
}
