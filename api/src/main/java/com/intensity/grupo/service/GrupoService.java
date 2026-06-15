package com.intensity.grupo.service;

import com.intensity.common.AccessMode;
import com.intensity.common.exception.ApiException;
import com.intensity.config.JwtService;
import com.intensity.grupo.dto.GroupMemberResponse;
import com.intensity.grupo.dto.JointAuthSessionResponse;
import com.intensity.grupo.entity.Grupo;
import com.intensity.grupo.entity.GrupoParticipante;
import com.intensity.grupo.repository.GrupoParticipanteRepository;
import com.intensity.grupo.repository.GrupoRepository;
import com.intensity.participante.entity.Participante;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class GrupoService {

	private final GrupoRepository grupoRepository;
	private final GrupoParticipanteRepository grupoParticipanteRepository;
	private final JwtService jwtService;

	public GrupoService(
			GrupoRepository grupoRepository,
			GrupoParticipanteRepository grupoParticipanteRepository,
			JwtService jwtService) {
		this.grupoRepository = grupoRepository;
		this.grupoParticipanteRepository = grupoParticipanteRepository;
		this.jwtService = jwtService;
	}

	@Transactional
	public JointAuthSessionResponse openExperienceBoxSession(List<Participante> participantes) {
		Map<UUID, Participante> uniqueById = new LinkedHashMap<>();
		for (Participante participante : participantes) {
			uniqueById.putIfAbsent(participante.getId(), participante);
		}

		List<Participante> members = uniqueById.values().stream()
				.sorted(Comparator.comparing(Participante::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.toList();

		List<UUID> participantIds = members.stream().map(Participante::getId).toList();

		Grupo grupo = resolveGroup(participantIds)
				.orElseGet(() -> createGroupWithMembers(participantIds));

		List<GroupMemberResponse> memberResponses = members.stream()
				.map(member -> new GroupMemberResponse(member.getId(), member.getDisplayName()))
				.toList();

		List<String> displayNames = members.stream().map(Participante::getDisplayName).toList();
		String token = jwtService.createExperienceBoxToken(grupo.getId(), participantIds, displayNames);

		return new JointAuthSessionResponse(
				token,
				grupo.getId(),
				memberResponses,
				AccessMode.EXPERIENCE_BOX);
	}

	private Optional<Grupo> resolveGroup(List<UUID> participantIds) {
		return grupoRepository
				.findGroupIdByExactMembers(participantIds, participantIds.size())
				.or(() -> grupoRepository
						.findGroupIdsContainingAllParticipants(participantIds, participantIds.size())
						.stream()
						.findFirst())
				.flatMap(grupoRepository::findById);
	}

	private Grupo createGroupWithMembers(List<UUID> participantIds) {
		assertNoGroupMembershipConflict(participantIds);

		Grupo grupo = Grupo.createNew();
		grupoRepository.save(grupo);

		List<GrupoParticipante> memberships = new ArrayList<>();
		for (UUID participantId : participantIds) {
			memberships.add(new GrupoParticipante(grupo, participantId));
		}
		grupoParticipanteRepository.saveAll(memberships);

		return grupo;
	}

	private void assertNoGroupMembershipConflict(List<UUID> participantIds) {
		Map<UUID, List<UUID>> groupsByParticipant = new LinkedHashMap<>();
		Set<UUID> distinctGroups = new HashSet<>();

		for (UUID participantId : participantIds) {
			List<UUID> groupIds = grupoParticipanteRepository.findGroupIdsByParticipantId(participantId);
			groupsByParticipant.put(participantId, groupIds);
			distinctGroups.addAll(groupIds);
		}

		for (int i = 0; i < participantIds.size(); i++) {
			List<UUID> groupsLeft = groupsByParticipant.get(participantIds.get(i));
			if (groupsLeft.isEmpty()) {
				continue;
			}
			for (int j = i + 1; j < participantIds.size(); j++) {
				List<UUID> groupsRight = groupsByParticipant.get(participantIds.get(j));
				if (groupsRight.isEmpty()) {
					continue;
				}
				if (Collections.disjoint(groupsLeft, groupsRight)) {
					throw groupMembershipConflict();
				}
			}
		}

		for (UUID groupId : distinctGroups) {
			long loggingInMembers = participantIds.stream()
					.filter(participantId -> grupoParticipanteRepository.existsById_GrupoIdAndId_ParticipanteId(
							groupId, participantId))
					.count();
			if (loggingInMembers >= 2 && loggingInMembers < participantIds.size()) {
				throw groupMembershipConflict();
			}
		}
	}

	private ApiException groupMembershipConflict() {
		return new ApiException(
				HttpStatus.CONFLICT,
				"GROUP_MEMBERSHIP_CONFLICT",
				"Credentials belong to different groups.");
	}
}
