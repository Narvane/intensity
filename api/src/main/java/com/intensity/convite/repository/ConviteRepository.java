package com.intensity.convite.repository;

import com.intensity.convite.entity.Convite;
import com.intensity.convite.entity.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConviteRepository extends JpaRepository<Convite, UUID> {

	boolean existsByCode(String code);

	Optional<Convite> findByCode(String code);

	Optional<Convite> findByLinkToken(UUID linkToken);

	List<Convite> findByGrupo_IdAndStatusOrderByCreatedAtDesc(UUID groupId, InviteStatus status);
}
