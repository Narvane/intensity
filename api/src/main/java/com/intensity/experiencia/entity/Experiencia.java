package com.intensity.experiencia.entity;

import com.intensity.caixinha.entity.Caixinha;
import com.intensity.participante.entity.Participante;
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
@Table(name = "experiencia")
public class Experiencia {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "caixinha_id", nullable = false)
	private Caixinha caixinha;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "participante_id", nullable = false)
	private Participante author;

	@Column(nullable = false, length = 1000)
	private String description;

	@Column(nullable = false, length = 2000)
	private String reflection;

	@Column(nullable = false)
	private int intensity;

	@Column(nullable = false)
	private int effort;

	@Column(nullable = false)
	private int openness;

	@Column(nullable = false)
	private int novelty;

	@Column(nullable = false, length = 16)
	private String seal;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Experiencia() {
	}

	public Experiencia(
			Caixinha caixinha,
			Participante author,
			String description,
			String reflection,
			int intensity,
			int effort,
			int openness,
			int novelty,
			String seal) {
		this.id = UUID.randomUUID();
		this.caixinha = caixinha;
		this.author = author;
		this.description = description.trim();
		this.reflection = reflection.trim();
		this.intensity = intensity;
		this.effort = effort;
		this.openness = openness;
		this.novelty = novelty;
		this.seal = seal;
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	public UUID getId() {
		return id;
	}

	public Caixinha getCaixinha() {
		return caixinha;
	}

	public Participante getAuthor() {
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

	public int getOpenness() {
		return openness;
	}

	public int getNovelty() {
		return novelty;
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
			int openness,
			int novelty,
			String seal) {
		this.description = description.trim();
		this.reflection = reflection.trim();
		this.intensity = intensity;
		this.effort = effort;
		this.openness = openness;
		this.novelty = novelty;
		this.seal = seal;
		this.updatedAt = Instant.now();
	}
}
