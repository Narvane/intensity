package com.intensity.box.service;

import com.intensity.box.dto.BoxResponse;
import com.intensity.box.dto.CreateBoxRequest;
import com.intensity.box.dto.UpdateBoxRequest;
import com.intensity.box.entity.Box;
import com.intensity.box.repository.BoxRepository;
import com.intensity.platform.common.AccessMode;
import com.intensity.platform.common.AuthPrincipal;
import com.intensity.platform.common.exception.ApiException;
import com.intensity.group.entity.Group;
import com.intensity.experience.repository.ExperienceRepository;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BoxService {

	private final BoxRepository boxRepository;
	private final GroupRepository groupRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final ExperienceRepository experienceRepository;

	public BoxService(
			BoxRepository boxRepository,
			GroupRepository groupRepository,
			GroupParticipantRepository groupParticipantRepository,
			ExperienceRepository experienceRepository) {
		this.boxRepository = boxRepository;
		this.groupRepository = groupRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.experienceRepository = experienceRepository;
	}

	@Transactional(readOnly = true)
	public List<BoxResponse> listByGroup(UUID groupId, AuthPrincipal principal) {
		ensureCanAccessGroup(groupId, principal);
		ensureGroupExists(groupId);

		boolean allGroupMembersPresent = isAllGroupMembersPresent(groupId, principal);

		return boxRepository.findAllByGroup_IdOrderByCreatedAtDesc(groupId).stream()
				.filter(box -> isBoxAvailable(box, allGroupMembersPresent))
				.map(box -> toResponse(box, principal))
				.toList();
	}

	@Transactional
	public BoxResponse create(CreateBoxRequest request, AuthPrincipal principal) {
		UUID groupId = request.groupId();
		ensureCanCreateBox(groupId, principal);
		Group group = ensureGroupExists(groupId);

		boolean requireAllParticipants = Boolean.TRUE.equals(request.requireAllParticipants());
		Box box = new Box(group, request.name(), request.type(), requireAllParticipants);
		boxRepository.save(box);

		return toResponse(box, principal);
	}

	@Transactional
	public BoxResponse update(UUID boxId, UpdateBoxRequest request, AuthPrincipal principal) {
		if (principal.accessMode() != AccessMode.EXPERIENCE_BOX) {
			throw forbidden();
		}

		Box box = boxRepository
				.findById(boxId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "BOX_NOT_FOUND", "Box not found."));

		if (!principal.canAccessExperienceBoxGroup(box.getGroup().getId())) {
			throw forbidden();
		}

		boolean requireAllParticipants = Boolean.TRUE.equals(request.requireAllParticipants());
		box.updateSettings(request.name(), requireAllParticipants);
		return toResponse(box, principal);
	}

	@Transactional
	public void delete(UUID boxId, AuthPrincipal principal) {
		if (principal.accessMode() != AccessMode.EXPERIENCE_BOX) {
			throw forbidden();
		}

		Box box = boxRepository
				.findById(boxId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "BOX_NOT_FOUND", "Box not found."));

		if (!principal.canAccessExperienceBoxGroup(box.getGroup().getId())) {
			throw forbidden();
		}

		boxRepository.delete(box);
	}

	private Group ensureGroupExists(UUID groupId) {
		return groupRepository
				.findById(groupId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found."));
	}

	private void ensureCanAccessGroup(UUID groupId, AuthPrincipal principal) {
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX) {
			if (!principal.canAccessExperienceBoxGroup(groupId)) {
				throw forbidden();
			}
			return;
		}

		ensureMember(groupId, principal.participantId());
	}

	private void ensureCanCreateBox(UUID groupId, AuthPrincipal principal) {
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX) {
			if (!principal.canAccessExperienceBoxGroup(groupId)) {
				throw forbidden();
			}
			return;
		}

		ensureMember(groupId, principal.participantId());
	}

	private void ensureMember(UUID groupId, UUID participantId) {
		if (!groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(groupId, participantId)) {
			throw forbidden();
		}
	}

	private BoxResponse toResponse(Box box, AuthPrincipal principal) {
		long experienceCount = experienceRepository.countByBox_Id(box.getId());
		long myExperienceCount = experienceRepository.countByBox_IdAndAuthor_Id(
				box.getId(), principal.participantId());
		return new BoxResponse(
				box.getId(),
				box.getGroup().getId(),
				box.getName(),
				box.getType(),
				box.isRequireAllParticipants(),
				box.getCreatedAt(),
				experienceCount,
				myExperienceCount);
	}

	private boolean isAllGroupMembersPresent(UUID groupId, AuthPrincipal principal) {
		if (principal.accessMode() != AccessMode.EXPERIENCE_BOX) {
			return true;
		}

		long memberCount = groupParticipantRepository.countMembersByGroupId(groupId);
		return principal.participantIds().size() >= memberCount;
	}

	private boolean isBoxAvailable(Box box, boolean allGroupMembersPresent) {
		if (!box.isRequireAllParticipants()) {
			return true;
		}

		return allGroupMembersPresent;
	}

	private ApiException forbidden() {
		return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Not allowed for current session.");
	}
}
