package com.intensity.group.service;

import com.intensity.common.AccessMode;
import com.intensity.common.AuthPrincipal;
import com.intensity.common.exception.ApiException;
import com.intensity.group.dto.GroupResponse;
import com.intensity.group.entity.Group;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
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

	public GroupQueryService(
			GroupRepository groupRepository,
			GroupParticipantRepository groupParticipantRepository) {
		this.groupRepository = groupRepository;
		this.groupParticipantRepository = groupParticipantRepository;
	}

	@Transactional(readOnly = true)
	public List<GroupResponse> listForPrincipal(AuthPrincipal principal) {
		if (principal.accessMode() == AccessMode.EXPERIENCE_BOX) {
			return groupRepository
					.findById(principal.groupId())
					.map(group -> List.of(new GroupResponse(
							group.getId(),
							(int) groupParticipantRepository.countMembersByGroupId(group.getId()),
							group.getCreatedAt())))
					.orElseThrow(() -> new ApiException(
							HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found."));
		}

		return groupParticipantRepository.findGroupIdsByParticipantId(principal.participantId()).stream()
				.map(groupId -> groupRepository
						.findById(groupId)
						.orElseThrow(() -> new ApiException(
								HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "Group not found.")))
				.sorted(Comparator.comparing(Group::getCreatedAt).reversed())
				.map(group -> new GroupResponse(
						group.getId(),
						(int) groupParticipantRepository.countMembersByGroupId(group.getId()),
						group.getCreatedAt()))
				.toList();
	}
}
