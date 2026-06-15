package com.intensity.participante.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "participante")
public class Participante {

	@Id
	private UUID id;

	@Column(name = "display_name", nullable = false, length = 80)
	private String displayName;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected Participante() {
	}

	public Participante(String displayName, String email, String passwordHash) {
		this.id = UUID.randomUUID();
		this.displayName = displayName;
		this.email = email.toLowerCase();
		this.passwordHash = passwordHash;
		this.createdAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
