package com.intensity.invite.repository;

import com.intensity.invite.entity.Invite;
import com.intensity.invite.entity.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InviteRepository extends JpaRepository<Invite, UUID> {

	boolean existsByCode(String code);

	Optional<Invite> findByCode(String code);

	Optional<Invite> findByLinkToken(UUID linkToken);

	List<Invite> findByGroup_IdAndStatusOrderByCreatedAtDesc(UUID groupId, InviteStatus status);
}
