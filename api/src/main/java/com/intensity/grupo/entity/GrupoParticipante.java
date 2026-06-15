package com.intensity.grupo.entity;

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
@Table(name = "grupo_participante")
public class GrupoParticipante {

	@EmbeddedId
	private Id id;

	@MapsId("grupoId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "grupo_id", nullable = false)
	private Grupo grupo;

	@Column(name = "joined_at", nullable = false)
	private Instant joinedAt;

	protected GrupoParticipante() {
	}

	public GrupoParticipante(Grupo grupo, UUID participanteId) {
		this.id = new Id(grupo.getId(), participanteId);
		this.grupo = grupo;
		this.joinedAt = Instant.now();
	}

	public Id getId() {
		return id;
	}

	public Grupo getGrupo() {
		return grupo;
	}

	@Embeddable
	public record Id(UUID grupoId, UUID participanteId) implements Serializable {
	}
}
