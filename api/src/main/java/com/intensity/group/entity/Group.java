package com.intensity.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "\"group\"")
public class Group {

	@Id
	private UUID id;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected Group() {
	}

	public Group(UUID id, Instant createdAt) {
		this.id = id;
		this.createdAt = createdAt;
	}

	public static Group createNew() {
		return new Group(UUID.randomUUID(), Instant.now());
	}

	public UUID getId() {
		return id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
