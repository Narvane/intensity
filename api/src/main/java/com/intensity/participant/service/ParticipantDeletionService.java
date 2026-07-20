package com.intensity.participant.service;

import com.intensity.experience.repository.ExperienceRepository;
import com.intensity.group.entity.GroupParticipant;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
import com.intensity.invite.repository.InviteRepository;
import com.intensity.participant.dto.LoginRequest;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ParticipantDeletionService {

	private final ParticipantService participantService;
	private final ParticipantRepository participantRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final GroupRepository groupRepository;
	private final ExperienceRepository experienceRepository;
	private final InviteRepository inviteRepository;

	public ParticipantDeletionService(
			ParticipantService participantService,
			ParticipantRepository participantRepository,
			GroupParticipantRepository groupParticipantRepository,
			GroupRepository groupRepository,
			ExperienceRepository experienceRepository,
			InviteRepository inviteRepository) {
		this.participantService = participantService;
		this.participantRepository = participantRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.groupRepository = groupRepository;
		this.experienceRepository = experienceRepository;
		this.inviteRepository = inviteRepository;
	}

	@Transactional
	public void deleteAccount(LoginRequest request) {
		Participant participant = participantService.authenticate(request);
		deleteParticipant(participant.getId());
	}

	@Transactional
	public void deleteParticipant(UUID participantId) {
		List<UUID> groupIds = new ArrayList<>(groupParticipantRepository.findGroupIdsByParticipantId(participantId));
		for (UUID groupId : groupIds) {
			removeFromGroup(groupId, participantId);
		}

		inviteRepository.deleteByCreator_Id(participantId);
		inviteRepository.deleteByAcceptor_Id(participantId);
		experienceRepository.deleteByAuthor_Id(participantId);
		participantRepository.deleteById(participantId);
	}

	private void removeFromGroup(UUID groupId, UUID participantId) {
		if (!groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(groupId, participantId)) {
			return;
		}

		long memberCount = groupParticipantRepository.countMembersByGroupId(groupId);
		if (memberCount <= 1) {
			groupRepository.deleteById(groupId);
			return;
		}

		experienceRepository.deleteByAuthor_IdInAndBox_Group_Id(List.of(participantId), groupId);
		groupParticipantRepository.deleteById(new GroupParticipant.Id(groupId, participantId));
	}
}
