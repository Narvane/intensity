package com.intensity.grupo.service;

import com.intensity.common.AccessMode;
import com.intensity.config.JwtService;
import com.intensity.grupo.dto.GroupMemberResponse;
import com.intensity.grupo.dto.JointAuthSessionResponse;
import com.intensity.grupo.entity.Grupo;
import com.intensity.grupo.entity.GrupoParticipante;
import com.intensity.grupo.repository.GrupoParticipanteRepository;
import com.intensity.grupo.repository.GrupoRepository;
import com.intensity.participante.entity.Participante;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

		Grupo grupo = grupoRepository
				.findGroupIdByExactMembers(participantIds, participantIds.size())
				.flatMap(grupoRepository::findById)
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

	private Grupo createGroupWithMembers(List<UUID> participantIds) {
		Grupo grupo = Grupo.createNew();
		grupoRepository.save(grupo);

		List<GrupoParticipante> memberships = new ArrayList<>();
		for (UUID participantId : participantIds) {
			memberships.add(new GrupoParticipante(grupo, participantId));
		}
		grupoParticipanteRepository.saveAll(memberships);

		return grupo;
	}
}
