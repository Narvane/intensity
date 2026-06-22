package com.intensity.invite.entity;

import com.intensity.group.entity.Group;
import com.intensity.participant.entity.Participant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invite")
public class Invite {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "creator_id", nullable = false)
	private Participant creator;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "acceptor_id")
	private Participant acceptor;

	@Column(nullable = false, length = 6)
	private String code;

	@Column(name = "link_token", nullable = false)
	private UUID linkToken;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private InviteStatus status;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "accepted_at")
	private Instant acceptedAt;

	protected Invite() {
	}

	public Invite(
			Group group,
			Participant creator,
			String code,
			UUID linkToken,
			Instant expiresAt,
			Instant createdAt) {
		this.id = UUID.randomUUID();
		this.group = group;
		this.creator = creator;
		this.code = normalizeCode(code);
		this.linkToken = linkToken;
		this.status = InviteStatus.ACTIVE;
		this.expiresAt = expiresAt;
		this.createdAt = createdAt;
	}

	public UUID getId() {
		return id;
	}

	public Group getGroup() {
		return group;
	}

	public Participant getCreator() {
		return creator;
	}

	public Participant getAcceptor() {
		return acceptor;
	}

	public String getCode() {
		return code;
	}

	public UUID getLinkToken() {
		return linkToken;
	}

	public InviteStatus getStatus() {
		return status;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getAcceptedAt() {
		return acceptedAt;
	}

	public InviteStatus effectiveStatus(Instant now) {
		if (status == InviteStatus.ACTIVE && now.isAfter(expiresAt)) {
			return InviteStatus.EXPIRED;
		}
		return status;
	}

	public boolean canBeAccepted(Instant now) {
		return effectiveStatus(now) == InviteStatus.ACTIVE;
	}

	public void markRevoked() {
		if (status != InviteStatus.ACTIVE) {
			throw new IllegalStateException("Only active invites can be revoked.");
		}
		this.status = InviteStatus.REVOKED;
	}

	public void markAccepted(Participant acceptor, Instant acceptedAt) {
		if (status != InviteStatus.ACTIVE) {
			throw new IllegalStateException("Only active invites can be accepted.");
		}
		this.status = InviteStatus.ACCEPTED;
		this.acceptor = acceptor;
		this.acceptedAt = acceptedAt;
	}

	public void markExpired() {
		if (status != InviteStatus.ACTIVE) {
			throw new IllegalStateException("Only active invites can expire.");
		}
		this.status = InviteStatus.EXPIRED;
	}

	public static String normalizeCode(String code) {
		return code.trim().toUpperCase();
	}
}
