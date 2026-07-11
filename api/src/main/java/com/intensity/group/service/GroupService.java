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
		return openExperienceBoxSession(participants, null, false);
	}

	@Transactional
	public JointAuthSessionResponse openExperienceBoxSession(
			List<Participant> participants, UUID targetGroupId, boolean requireAllMembers) {
		Map<UUID, Participant> uniqueById = new LinkedHashMap<>();
		for (Participant participant : participants) {
			uniqueById.putIfAbsent(participant.getId(), participant);
		}

		List<Participant> members = uniqueById.values().stream()
				.sorted(Comparator.comparing(Participant::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.toList();

		List<UUID> participantIds = members.stream().map(Participant::getId).toList();

		List<UUID> groupIds;
		if (targetGroupId != null) {
			groupIds = List.of(openTargetedGroupSession(targetGroupId, participantIds, requireAllMembers));
		} else {
			groupIds = resolveGroupIds(participantIds);
			if (groupIds.isEmpty()) {
				groupIds = List.of(createGroupWithMembers(participantIds).getId());
			}
		}

		List<GroupMemberResponse> memberResponses = members.stream()
				.map(member -> new GroupMemberResponse(
						member.getId(), member.getDisplayName(), member.getEmail()))
				.toList();

		List<String> displayNames = members.stream().map(Participant::getDisplayName).toList();
		String token = jwtService.createExperienceBoxToken(groupIds, participantIds, displayNames);

		return new JointAuthSessionResponse(
				token,
				groupIds.getFirst(),
				groupIds,
				memberResponses,
				AccessMode.EXPERIENCE_BOX);
	}

	private UUID openTargetedGroupSession(
			UUID groupId, List<UUID> participantIds, boolean requireAllMembers) {
		ensureGroupExists(groupId);

		List<UUID> memberIds = groupParticipantRepository.findParticipantIdsByGroupId(groupId);
		Set<UUID> membership = new HashSet<>(memberIds);

		for (UUID participantId : participantIds) {
			if (!membership.contains(participantId)) {
				throw new ApiException(
						HttpStatus.CONFLICT,
						"GROUP_TARGET_MISMATCH",
						"Credentials do not belong to the selected group.");
			}
		}

		if (requireAllMembers && participantIds.size() != memberIds.size()) {
			throw new ApiException(
					HttpStatus.UNPROCESSABLE_ENTITY,
					"GROUP_REQUIRES_ALL_MEMBERS",
					"All group members must authenticate for this session.");
		}

		return groupId;
	}

	private Group ensureGroupExists(UUID groupId) {
		return groupRepository
				.findById(groupId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found."));
	}

	private List<UUID> resolveGroupIds(List<UUID> participantIds) {
		List<UUID> exactMatches =
				groupRepository.findGroupIdsByExactMembers(participantIds, participantIds.size());
		if (!exactMatches.isEmpty()) {
			return sortGroupIds(exactMatches);
		}

		List<UUID> containingMatches = groupRepository.findGroupIdsContainingAllParticipants(
				participantIds, participantIds.size());
		if (!containingMatches.isEmpty()) {
			return sortGroupIds(containingMatches);
		}

		return List.of();
	}

	private List<UUID> sortGroupIds(List<UUID> groupIds) {
		return groupRepository.findAllById(groupIds).stream()
				.sorted(Comparator.comparing(Group::getCreatedAt))
				.map(Group::getId)
				.toList();
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
