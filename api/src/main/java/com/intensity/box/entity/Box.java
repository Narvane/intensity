package com.intensity.box.entity;

import com.intensity.group.entity.Group;
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
@Table(name = "box")
public class Box {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Column(nullable = false, length = 80)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private BoxType type;

	@Column(name = "require_all_participants", nullable = false)
	private boolean requireAllParticipants;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected Box() {
	}

	public Box(Group group, String name, BoxType type, boolean requireAllParticipants) {
		this.id = UUID.randomUUID();
		this.group = group;
		this.name = name.trim();
		this.type = type == null ? BoxType.SAIDAS_COM_AMIGOS : type;
		this.requireAllParticipants = requireAllParticipants;
		this.createdAt = Instant.now();
	}

	public boolean isRequireAllParticipants() {
		return requireAllParticipants;
	}

	public void updateSettings(String name, boolean requireAllParticipants) {
		this.name = name.trim();
		this.requireAllParticipants = requireAllParticipants;
	}

	public UUID getId() {
		return id;
	}

	public Group getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public BoxType getType() {
		return type;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
