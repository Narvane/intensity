package com.intensity.participant.repository;

import com.intensity.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

	Optional<Participant> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
