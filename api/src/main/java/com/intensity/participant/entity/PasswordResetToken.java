package com.intensity.participant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "participant_id", nullable = false)
	private Participant participant;

	@Column(nullable = false, unique = true)
	private UUID token;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "used_at")
	private Instant usedAt;

	protected PasswordResetToken() {
	}

	public PasswordResetToken(Participant participant, UUID token, Instant expiresAt, Instant createdAt) {
		this.id = UUID.randomUUID();
		this.participant = participant;
		this.token = token;
		this.expiresAt = expiresAt;
		this.createdAt = createdAt;
	}

	public UUID getId() {
		return id;
	}

	public Participant getParticipant() {
		return participant;
	}

	public UUID getToken() {
		return token;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUsedAt() {
		return usedAt;
	}

	public boolean isUsable(Instant now) {
		return usedAt == null && !now.isAfter(expiresAt);
	}

	public void markUsed(Instant usedAt) {
		if (this.usedAt != null) {
			throw new IllegalStateException("Reset token already used.");
		}
		this.usedAt = usedAt;
	}
}
