package com.intensity.grupo.service;

import com.intensity.common.AccessMode;
import com.intensity.common.AuthPrincipal;
import com.intensity.common.exception.ApiException;
import com.intensity.grupo.entity.Grupo;
import com.intensity.grupo.entity.GrupoParticipante;
import com.intensity.grupo.repository.GrupoParticipanteRepository;
import com.intensity.grupo.repository.GrupoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GrupoMembershipService {

	private final GrupoRepository grupoRepository;
	private final GrupoParticipanteRepository grupoParticipanteRepository;

	public GrupoMembershipService(
			GrupoRepository grupoRepository,
			GrupoParticipanteRepository grupoParticipanteRepository) {
		this.grupoRepository = grupoRepository;
		this.grupoParticipanteRepository = grupoParticipanteRepository;
	}

	@Transactional
	public void leave(UUID groupId, AuthPrincipal principal) {
		ensureGroupExists(groupId);
		ensureCanLeave(groupId, principal);

		List<UUID> activeLeavers = resolveActiveLeavers(groupId, principal);
		if (activeLeavers.isEmpty()) {
			throw forbidden();
		}

		long memberCount = grupoParticipanteRepository.countMembersByGroupId(groupId);
		if (memberCount <= activeLeavers.size()) {
			grupoRepository.deleteById(groupId);
			return;
		}

		for (UUID participantId : activeLeavers) {
			grupoParticipanteRepository.deleteById(new GrupoParticipante.Id(groupId, participantId));
		}
	}

	private List<UUID> resolveActiveLeavers(UUID groupId, AuthPrincipal principal) {
		Set<UUID> leavers = new LinkedHashSet<>();
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX) {
			leavers.addAll(principal.participantIds());
		} else {
			leavers.add(principal.participantId());
		}

		List<UUID> activeLeavers = new ArrayList<>();
		for (UUID participantId : leavers) {
			if (grupoParticipanteRepository.existsById_GrupoIdAndId_ParticipanteId(groupId, participantId)) {
				activeLeavers.add(participantId);
			}
		}
		return activeLeavers;
	}

	private Grupo ensureGroupExists(UUID groupId) {
		return grupoRepository
				.findById(groupId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found."));
	}

	private void ensureCanLeave(UUID groupId, AuthPrincipal principal) {
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX) {
			if (!groupId.equals(principal.groupId())) {
				throw forbidden();
			}
			return;
		}

		if (!grupoParticipanteRepository.existsById_GrupoIdAndId_ParticipanteId(
				groupId, principal.participantId())) {
			throw forbidden();
		}
	}

	private ApiException forbidden() {
		return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Not allowed for current session.");
	}
}
