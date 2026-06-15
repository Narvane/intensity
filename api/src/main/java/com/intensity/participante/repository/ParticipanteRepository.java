package com.intensity.participante.repository;

import com.intensity.participante.entity.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParticipanteRepository extends JpaRepository<Participante, UUID> {

	Optional<Participante> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
