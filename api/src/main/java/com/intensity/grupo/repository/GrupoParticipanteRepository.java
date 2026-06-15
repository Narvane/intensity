package com.intensity.grupo.repository;

import com.intensity.grupo.entity.GrupoParticipante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GrupoParticipanteRepository extends JpaRepository<GrupoParticipante, GrupoParticipante.Id> {
}
