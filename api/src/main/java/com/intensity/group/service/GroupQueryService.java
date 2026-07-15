package com.intensity.group.service;

import com.intensity.platform.common.AccessMode;
import com.intensity.platform.common.AuthPrincipal;
import com.intensity.platform.common.exception.ApiException;
import com.intensity.group.GroupColor;
import com.intensity.group.dto.CreateGroupRequest;
import com.intensity.group.dto.GroupMemberResponse;
import com.intensity.group.dto.GroupResponse;
import com.intensity.group.dto.UpdateGroupRequest;
import com.intensity.group.entity.Group;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.ParticipantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GroupQueryService {

	private final GroupRepository groupRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final ParticipantRepository participantRepository;
	private final GroupCreationService groupCreationService;

	public GroupQueryService(
			GroupRepository groupRepository,
			GroupParticipantRepository groupParticipantRepository,
			ParticipantRepository participantRepository,
			GroupCreationService groupCreationService) {
		this.groupRepository = groupRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.participantRepository = participantRepository;
		this.groupCreationService = groupCreationService;
	}

	@Transactional
	public List<GroupResponse> listForPrincipal(AuthPrincipal principal) {
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX) {
			return principal.groupIds().stream()
					.map(groupId -> groupRepository
							.findById(groupId)
							.orElseThrow(() -> new ApiException(
									HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found.")))
					.sorted(Comparator.comparing(Group::getCreatedAt))
					.map(this::toResponse)
					.toList();
		}

		ensureDefaultGroupIfEmpty(principal.participantId());

		return groupParticipantRepository.findGroupIdsByParticipantId(principal.participantId()).stream()
				.map(groupId -> groupRepository
						.findById(groupId)
						.orElseThrow(() -> new ApiException(
								HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found.")))
				.sorted(Comparator.comparing(Group::getCreatedAt).reversed())
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public GroupResponse createForPrincipal(AuthPrincipal principal, CreateGroupRequest request) {
		ensureExperiencesMode(principal);
		String name = request == null ? "" : normalizeName(request.name(), true);
		String color = GroupColor.normalize(request != null ? request.color() : null);
		Group group = groupCreationService.createSoloGroup(principal.participantId(), name, color);
		return toResponse(group);
	}

	@Transactional
	public GroupResponse updateForPrincipal(
			AuthPrincipal principal, UUID groupId, UpdateGroupRequest request) {
		ensureGroupAccess(principal, groupId);
		ensureMember(groupId, principal.participantId());

		Group group = groupRepository
				.findById(groupId)
				.orElseThrow(() -> new ApiException(
						HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found."));

		String name = normalizeName(request.name(), true);
		String color = GroupColor.normalize(request.color());
		group.updateIdentity(name, color);
		return toResponse(group);
	}

	public GroupResponse toResponse(Group group) {
		List<GroupMemberResponse> members = listMembers(group.getId());
		return new GroupResponse(
				group.getId(),
				group.getName(),
				group.getColor(),
				members.size(),
				group.getCreatedAt(),
				members);
	}

	private List<GroupMemberResponse> listMembers(UUID groupId) {
		List<UUID> participantIds = groupParticipantRepository.findParticipantIdsByGroupId(groupId);
		return participantRepository.findAllById(participantIds).stream()
				.sorted(Comparator.comparing(Participant::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.map(participant -> new GroupMemberResponse(
						participant.getId(), participant.getDisplayName(), participant.getEmail()))
				.toList();
	}

	private void ensureDefaultGroupIfEmpty(UUID participantId) {
		if (groupParticipantRepository.findGroupIdsByParticipantId(participantId).isEmpty()) {
			groupCreationService.createSoloGroup(participantId);
		}
	}

	private void ensureMember(UUID groupId, UUID participantId) {
		if (!groupParticipantRepository.existsById_GroupIdAndId_ParticipantId(groupId, participantId)) {
			throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Not allowed for current session.");
		}
	}

	private void ensureGroupAccess(AuthPrincipal principal, UUID groupId) {
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX && !principal.canAccessExperienceBoxGroup(groupId)) {
			throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Not allowed for current session.");
		}
	}

	private void ensureExperiencesMode(AuthPrincipal principal) {
		if (principal.accessMode() != AccessMode.EXPERIENCES) {
			throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Not allowed for current session.");
		}
	}

	private String normalizeName(String raw, boolean required) {
		String name = raw == null ? "" : raw.trim();
		if (required && name.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_GROUP_NAME", "Group name is required.");
		}
		if (name.length() > 120) {
			throw new ApiException(
					HttpStatus.BAD_REQUEST, "INVALID_GROUP_NAME", "Group name must be at most 120 characters.");
		}
		return name;
	}
}
