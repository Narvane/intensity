package com.intensity.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "group_participant")
public class GroupParticipant {

	@EmbeddedId
	private Id id;

	@MapsId("groupId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Column(name = "joined_at", nullable = false)
	private Instant joinedAt;

	protected GroupParticipant() {
	}

	public GroupParticipant(Group group, UUID participantId) {
		this.id = new Id(group.getId(), participantId);
		this.group = group;
		this.joinedAt = Instant.now();
	}

	public Id getId() {
		return id;
	}

	public Group getGroup() {
		return group;
	}

	@Embeddable
	public record Id(UUID groupId, UUID participantId) implements Serializable {
	}
}
