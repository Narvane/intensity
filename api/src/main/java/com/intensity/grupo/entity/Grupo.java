package com.intensity.grupo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grupo")
public class Grupo {

	@Id
	private UUID id;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected Grupo() {
	}

	public Grupo(UUID id, Instant createdAt) {
		this.id = id;
		this.createdAt = createdAt;
	}

	public static Grupo createNew() {
		return new Grupo(UUID.randomUUID(), Instant.now());
	}

	public UUID getId() {
		return id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
