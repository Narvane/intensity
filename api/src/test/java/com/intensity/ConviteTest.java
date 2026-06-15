package com.intensity;

import com.intensity.convite.entity.Convite;
import com.intensity.convite.entity.InviteStatus;
import com.intensity.grupo.entity.Grupo;
import com.intensity.participante.entity.Participante;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConviteTest {

	@Test
	void activeInviteExpiresAfterValidityWindow() {
		Convite convite = sampleInvite(
				Instant.parse("2026-06-15T12:00:00Z"),
				Instant.parse("2026-06-22T12:00:00Z"));

		assertEquals(
				InviteStatus.EXPIRED,
				convite.effectiveStatus(Instant.parse("2026-06-22T12:00:01Z")));
		assertFalse(convite.canBeAccepted(Instant.parse("2026-06-22T12:00:01Z")));
	}

	@Test
	void activeInviteCanBeAcceptedBeforeExpiry() {
		Convite convite = sampleInvite(
				Instant.parse("2026-06-15T12:00:00Z"),
				Instant.parse("2026-06-22T12:00:00Z"));

		assertTrue(convite.canBeAccepted(Instant.parse("2026-06-21T12:00:00Z")));
	}

	@Test
	void acceptedInviteRecordsAcceptor() {
		Convite convite = sampleInvite(
				Instant.parse("2026-06-15T12:00:00Z"),
				Instant.parse("2026-06-22T12:00:00Z"));
		Participante acceptor = new Participante("Bob", "bob@example.com", "hash");
		Instant acceptedAt = Instant.parse("2026-06-16T10:00:00Z");

		convite.markAccepted(acceptor, acceptedAt);

		assertEquals(InviteStatus.ACCEPTED, convite.getStatus());
		assertEquals(acceptor, convite.getAcceptor());
		assertEquals(acceptedAt, convite.getAcceptedAt());
	}

	@Test
	void revokedInviteCannotBeAccepted() {
		Convite convite = sampleInvite(
				Instant.parse("2026-06-15T12:00:00Z"),
				Instant.parse("2026-06-22T12:00:00Z"));
		convite.markRevoked();

		assertThrows(IllegalStateException.class, () -> convite.markAccepted(
				new Participante("Bob", "bob@example.com", "hash"),
				Instant.parse("2026-06-16T10:00:00Z")));
	}

	private Convite sampleInvite(Instant createdAt, Instant expiresAt) {
		Grupo grupo = Grupo.createNew();
		Participante creator = new Participante("Alice", "alice@example.com", "hash");
		return new Convite(grupo, creator, "AB23CD", UUID.randomUUID(), expiresAt, createdAt);
	}
}
