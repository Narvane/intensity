package com.intensity.group.service;

import com.intensity.group.GroupColor;
import com.intensity.group.entity.Group;
import com.intensity.group.entity.GroupParticipant;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GroupCreationService {

	private final GroupRepository groupRepository;
	private final GroupParticipantRepository groupParticipantRepository;

	public GroupCreationService(
			GroupRepository groupRepository,
			GroupParticipantRepository groupParticipantRepository) {
		this.groupRepository = groupRepository;
		this.groupParticipantRepository = groupParticipantRepository;
	}

	@Transactional
	public Group createSoloGroup(UUID participantId) {
		return createSoloGroup(participantId, "", GroupColor.DEFAULT);
	}

	@Transactional
	public Group createSoloGroup(UUID participantId, String name, String color) {
		Group group = Group.createNew(name, color);
		groupRepository.save(group);
		groupParticipantRepository.save(new GroupParticipant(group, participantId));
		return group;
	}
}
