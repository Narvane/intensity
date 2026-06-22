package com.intensity.group.repository;

import com.intensity.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

	@Query("""
			SELECT gp.id.groupId
			FROM GroupParticipant gp
			GROUP BY gp.id.groupId
			HAVING COUNT(gp) = :memberCount
			  AND SUM(CASE WHEN gp.id.participantId IN :participantIds THEN 1 ELSE 0 END) = :memberCount
			""")
	Optional<UUID> findGroupIdByExactMembers(
			@Param("participantIds") List<UUID> participantIds,
			@Param("memberCount") long memberCount);

	@Query("""
			SELECT gp.id.groupId
			FROM GroupParticipant gp
			WHERE gp.id.participantId IN :participantIds
			GROUP BY gp.id.groupId
			HAVING COUNT(DISTINCT gp.id.participantId) = :participantCount
			""")
	List<UUID> findGroupIdsContainingAllParticipants(
			@Param("participantIds") List<UUID> participantIds,
			@Param("participantCount") long participantCount);
}
