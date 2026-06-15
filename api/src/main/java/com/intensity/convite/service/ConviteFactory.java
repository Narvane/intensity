package com.intensity.convite.service;

import com.intensity.convite.entity.Convite;
import com.intensity.convite.repository.ConviteRepository;
import com.intensity.grupo.entity.Grupo;
import com.intensity.participante.entity.Participante;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ConviteFactory {

	private static final int MAX_CODE_ATTEMPTS = 12;

	private final ConviteRepository conviteRepository;
	private final InviteCodeGenerator inviteCodeGenerator;
	private final InviteExpirationPolicy inviteExpirationPolicy;

	public ConviteFactory(
			ConviteRepository conviteRepository,
			InviteCodeGenerator inviteCodeGenerator,
			InviteExpirationPolicy inviteExpirationPolicy) {
		this.conviteRepository = conviteRepository;
		this.inviteCodeGenerator = inviteCodeGenerator;
		this.inviteExpirationPolicy = inviteExpirationPolicy;
	}

	public Convite createNew(Grupo grupo, Participante creator) {
		Instant createdAt = Instant.now();
		Instant expiresAt = inviteExpirationPolicy.expiresAt(createdAt);
		String code = generateUniqueCode();
		UUID linkToken = UUID.randomUUID();

		return new Convite(grupo, creator, code, linkToken, expiresAt, createdAt);
	}

	private String generateUniqueCode() {
		for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
			String code = inviteCodeGenerator.generateCode();
			if (!conviteRepository.existsByCode(code)) {
				return code;
			}
		}

		throw new IllegalStateException("Could not generate a unique invite code.");
	}
}
