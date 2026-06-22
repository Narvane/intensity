package com.intensity.group.service;

import com.intensity.common.AccessMode;
import com.intensity.common.exception.ApiException;
import com.intensity.config.JwtService;
import com.intensity.group.dto.GroupMemberResponse;
import com.intensity.group.dto.JointAuthSessionResponse;
import com.intensity.group.entity.Group;
import com.intensity.group.entity.GroupParticipant;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
import com.intensity.participant.entity.Participant;
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
public class GroupService {

	private final GroupRepository groupRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final JwtService jwtService;

	public GroupService(
			GroupRepository groupRepository,
			GroupParticipantRepository groupParticipantRepository,
			JwtService jwtService) {
		this.groupRepository = groupRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.jwtService = jwtService;
	}

	@Transactional
	public JointAuthSessionResponse openExperienceBoxSession(List<Participant> participants) {
		Map<UUID, Participant> uniqueById = new LinkedHashMap<>();
		for (Participant participant : participants) {
			uniqueById.putIfAbsent(participant.getId(), participant);
		}

		List<Participant> members = uniqueById.values().stream()
				.sorted(Comparator.comparing(Participant::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.toList();

		List<UUID> participantIds = members.stream().map(Participant::getId).toList();

		Group group = resolveGroup(participantIds)
				.orElseGet(() -> createGroupWithMembers(participantIds));

		List<GroupMemberResponse> memberResponses = members.stream()
				.map(member -> new GroupMemberResponse(member.getId(), member.getDisplayName()))
				.toList();

		List<String> displayNames = members.stream().map(Participant::getDisplayName).toList();
		String token = jwtService.createExperienceBoxToken(group.getId(), participantIds, displayNames);

		return new JointAuthSessionResponse(
				token,
				group.getId(),
				memberResponses,
				AccessMode.EXPERIENCE_BOX);
	}

	private Optional<Group> resolveGroup(List<UUID> participantIds) {
		return groupRepository
				.findGroupIdByExactMembers(participantIds, participantIds.size())
				.or(() -> groupRepository
						.findGroupIdsContainingAllParticipants(participantIds, participantIds.size())
						.stream()
						.findFirst())
				.flatMap(groupRepository::findById);
	}

	private Group createGroupWithMembers(List<UUID> participantIds) {
		assertNoGroupMembershipConflict(participantIds);

		Group group = Group.createNew();
		groupRepository.save(group);

		List<GroupParticipant> memberships = new ArrayList<>();
		for (UUID participantId : participantIds) {
			memberships.add(new GroupParticipant(group, participantId));
		}
		groupParticipantRepository.saveAll(memberships);

		return group;
	}

	private void assertNoGroupMembershipConflict(List<UUID> participantIds) {
		Map<UUID, List<UUID>> groupsByParticipant = new LinkedHashMap<>();
		Set<UUID> distinctGroups = new HashSet<>();

		for (UUID participantId : participantIds) {
			List<UUID> groupIds = groupParticipantRepository.findGroupIdsByParticipantId(participantId);
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
					.filter(participantId -> groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(
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
