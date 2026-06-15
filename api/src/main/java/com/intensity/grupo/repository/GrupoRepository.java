package com.intensity.grupo.repository;

import com.intensity.grupo.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GrupoRepository extends JpaRepository<Grupo, UUID> {

	@Query("""
			SELECT gp.id.grupoId
			FROM GrupoParticipante gp
			GROUP BY gp.id.grupoId
			HAVING COUNT(gp) = :memberCount
			  AND SUM(CASE WHEN gp.id.participanteId IN :participantIds THEN 1 ELSE 0 END) = :memberCount
			""")
	Optional<UUID> findGroupIdByExactMembers(
			@Param("participantIds") List<UUID> participantIds,
			@Param("memberCount") long memberCount);
}
