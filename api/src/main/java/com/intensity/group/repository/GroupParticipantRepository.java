package com.intensity.group.repository;

import com.intensity.group.entity.GroupParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupParticipantRepository extends JpaRepository<GroupParticipant, GroupParticipant.Id> {

	boolean existsById_GroupIdAndId_ParticipantId(UUID groupId, UUID participantId);

	@Query("SELECT gp.id.groupId FROM GroupParticipant gp WHERE gp.id.participantId = :participantId")
	List<UUID> findGroupIdsByParticipantId(@Param("participantId") UUID participantId);

	@Query("SELECT COUNT(gp) FROM GroupParticipant gp WHERE gp.id.groupId = :groupId")
	long countMembersByGroupId(@Param("groupId") UUID groupId);

	@Query("SELECT gp.id.participantId FROM GroupParticipant gp WHERE gp.id.groupId = :groupId")
	List<UUID> findParticipantIdsByGroupId(@Param("groupId") UUID groupId);
}
