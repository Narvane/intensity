package com.intensity.group.service;

import com.intensity.platform.common.AccessMode;
import com.intensity.platform.common.AuthPrincipal;
import com.intensity.platform.common.exception.ApiException;
import com.intensity.experience.repository.ExperienceRepository;
import com.intensity.group.entity.Group;
import com.intensity.group.entity.GroupParticipant;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GroupMembershipService {

	private final GroupRepository groupRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final ExperienceRepository experienceRepository;

	public GroupMembershipService(
			GroupRepository groupRepository,
			GroupParticipantRepository groupParticipantRepository,
			ExperienceRepository experienceRepository) {
		this.groupRepository = groupRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.experienceRepository = experienceRepository;
	}

	@Transactional
	public void leave(UUID groupId, AuthPrincipal principal) {
		ensureGroupExists(groupId);
		ensureCanLeave(groupId, principal);

		List<UUID> activeLeavers = resolveActiveLeavers(groupId, principal);
		if (activeLeavers.isEmpty()) {
			throw forbidden();
		}

		long memberCount = groupParticipantRepository.countMembersByGroupId(groupId);
		if (memberCount <= activeLeavers.size()) {
			groupRepository.deleteById(groupId);
			return;
		}

		experienceRepository.deleteByAuthor_IdInAndBox_Group_Id(activeLeavers, groupId);

		for (UUID participantId : activeLeavers) {
			groupParticipantRepository.deleteById(new GroupParticipant.Id(groupId, participantId));
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
			if (groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(groupId, participantId)) {
				activeLeavers.add(participantId);
			}
		}
		return activeLeavers;
	}

	private Group ensureGroupExists(UUID groupId) {
		return groupRepository
				.findById(groupId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found."));
	}

	private void ensureCanLeave(UUID groupId, AuthPrincipal principal) {
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX) {
			if (!principal.canAccessExperienceBoxGroup(groupId)) {
				throw forbidden();
			}
			return;
		}

		if (!groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(
				groupId, principal.participantId())) {
			throw forbidden();
		}
	}

	private ApiException forbidden() {
		return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Not allowed for current session.");
	}
}
