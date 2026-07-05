package com.intensity.experience.entity;

import com.intensity.box.entity.Box;
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
@Table(name = "experience")
public class Experience {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "box_id", nullable = false)
	private Box box;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "participant_id", nullable = false)
	private Participant author;

	@Column(nullable = false, length = 1000)
	private String description;

	@Column(length = 2000)
	private String reflection;

	@Column(nullable = false)
	private int intensity;

	@Column(nullable = false)
	private int effort;

	@Column(nullable = false)
	private int unpredictability;

	@Column(nullable = false)
	private int novelty;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private ExperienceType type;

	@Column(nullable = false, length = 16)
	private String seal;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Experience() {
	}

	public Experience(
			Box box,
			Participant author,
			String description,
			String reflection,
			int intensity,
			int effort,
			int unpredictability,
			int novelty,
			ExperienceType type,
			String seal) {
		this.id = UUID.randomUUID();
		this.box = box;
		this.author = author;
		this.description = description.trim();
		this.reflection = normalizeReflection(reflection);
		this.intensity = intensity;
		this.effort = effort;
		this.unpredictability = unpredictability;
		this.novelty = novelty;
		this.type = type == null ? ExperienceType.NONE : type;
		this.seal = seal;
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	private static String normalizeReflection(String reflection) {
		if (reflection == null) {
			return null;
		}
		String trimmed = reflection.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	public UUID getId() {
		return id;
	}

	public Box getBox() {
		return box;
	}

	public Participant getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

	public String getReflection() {
		return reflection;
	}

	public int getIntensity() {
		return intensity;
	}

	public int getEffort() {
		return effort;
	}

	public int getUnpredictability() {
		return unpredictability;
	}

	public int getNovelty() {
		return novelty;
	}

	public ExperienceType getType() {
		return type;
	}

	public String getSeal() {
		return seal;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void updateContent(
			String description,
			String reflection,
			int intensity,
			int effort,
			int unpredictability,
			int novelty,
			ExperienceType type,
			String seal) {
		this.description = description.trim();
		this.reflection = normalizeReflection(reflection);
		this.intensity = intensity;
		this.effort = effort;
		this.unpredictability = unpredictability;
		this.novelty = novelty;
		this.type = type == null ? ExperienceType.NONE : type;
		this.seal = seal;
		this.updatedAt = Instant.now();
	}
}
