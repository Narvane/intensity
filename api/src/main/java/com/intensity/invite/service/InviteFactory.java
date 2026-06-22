package com.intensity.invite.service;

import com.intensity.invite.entity.Invite;
import com.intensity.invite.repository.InviteRepository;
import com.intensity.group.entity.Group;
import com.intensity.participant.entity.Participant;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class InviteFactory {

	private static final int MAX_CODE_ATTEMPTS = 12;

	private final InviteRepository inviteRepository;
	private final InviteCodeGenerator inviteCodeGenerator;
	private final InviteExpirationPolicy inviteExpirationPolicy;

	public InviteFactory(
			InviteRepository inviteRepository,
			InviteCodeGenerator inviteCodeGenerator,
			InviteExpirationPolicy inviteExpirationPolicy) {
		this.inviteRepository = inviteRepository;
		this.inviteCodeGenerator = inviteCodeGenerator;
		this.inviteExpirationPolicy = inviteExpirationPolicy;
	}

	public Invite createNew(Group group, Participant creator) {
		Instant createdAt = Instant.now();
		Instant expiresAt = inviteExpirationPolicy.expiresAt(createdAt);
		String code = generateUniqueCode();
		UUID linkToken = UUID.randomUUID();

		return new Invite(group, creator, code, linkToken, expiresAt, createdAt);
	}

	private String generateUniqueCode() {
		for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
			String code = inviteCodeGenerator.generateCode();
			if (!inviteRepository.existsByCode(code)) {
				return code;
			}
		}

		throw new IllegalStateException("Could not generate a unique invite code.");
	}
}
