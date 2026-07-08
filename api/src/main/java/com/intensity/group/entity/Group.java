package com.intensity.group.entity;

import com.intensity.group.GroupColor;
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

	@Column(name = "name", nullable = false, length = 120)
	private String name;

	@Column(name = "color", nullable = false, length = 20)
	private String color;

	protected Group() {
	}

	public Group(UUID id, Instant createdAt, String name, String color) {
		this.id = id;
		this.createdAt = createdAt;
		this.name = name;
		this.color = color;
	}

	public static Group createNew() {
		return new Group(UUID.randomUUID(), Instant.now(), "", GroupColor.DEFAULT);
	}

	public static Group createNew(String name, String color) {
		return new Group(UUID.randomUUID(), Instant.now(), name, color);
	}

	public void updateIdentity(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public UUID getId() {
		return id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}
}
