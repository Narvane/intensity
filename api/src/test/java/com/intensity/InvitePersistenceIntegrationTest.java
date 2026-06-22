package com.intensity;

import com.intensity.invite.entity.Invite;
import com.intensity.invite.entity.InviteStatus;
import com.intensity.invite.repository.InviteRepository;
import com.intensity.invite.service.InviteFactory;
import com.intensity.group.entity.Group;
import com.intensity.group.repository.GroupRepository;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.ParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InvitePersistenceIntegrationTest {

	@Autowired
	private InviteFactory inviteFactory;

	@Autowired
	private InviteRepository inviteRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ParticipantRepository participantRepository;

	@Test
	void persistsInviteWithUniqueCodeAndLinkToken() {
		Participant creator = participantRepository.save(
				new Participant("Dana", "dana@example.com", "hash"));
		Group group = groupRepository.save(Group.createNew());

		Invite invite = inviteFactory.createNew(group, creator);
		inviteRepository.save(invite);

		Invite byCode = inviteRepository.findByCode(invite.getCode()).orElseThrow();
		Invite byToken = inviteRepository.findByLinkToken(invite.getLinkToken()).orElseThrow();

		assertEquals(invite.getId(), byCode.getId());
		assertEquals(invite.getId(), byToken.getId());
		assertEquals(InviteStatus.ACTIVE, byCode.getStatus());
		assertTrue(byCode.getExpiresAt().isAfter(byCode.getCreatedAt()));
	}
}
