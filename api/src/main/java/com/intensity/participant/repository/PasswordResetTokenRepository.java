package com.intensity.participant.repository;

import com.intensity.participant.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

	Optional<PasswordResetToken> findByToken(UUID token);

	Optional<PasswordResetToken> findFirstByParticipant_EmailIgnoreCaseAndUsedAtIsNullOrderByCreatedAtDesc(
			String email);

	@Modifying(clearAutomatically = true)
	@Query("""
			delete from PasswordResetToken t
			where t.participant.id = :participantId and t.usedAt is null
			""")
	void deleteUnusedByParticipantId(@Param("participantId") UUID participantId);
}
