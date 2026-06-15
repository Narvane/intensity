package com.intensity;

import com.intensity.convite.entity.Convite;
import com.intensity.convite.entity.InviteStatus;
import com.intensity.convite.repository.ConviteRepository;
import com.intensity.convite.service.ConviteFactory;
import com.intensity.grupo.entity.Grupo;
import com.intensity.grupo.repository.GrupoRepository;
import com.intensity.participante.entity.Participante;
import com.intensity.participante.repository.ParticipanteRepository;
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
class ConvitePersistenceIntegrationTest {

	@Autowired
	private ConviteFactory conviteFactory;

	@Autowired
	private ConviteRepository conviteRepository;

	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private ParticipanteRepository participanteRepository;

	@Test
	void persistsInviteWithUniqueCodeAndLinkToken() {
		Participante creator = participanteRepository.save(
				new Participante("Dana", "dana@example.com", "hash"));
		Grupo grupo = grupoRepository.save(Grupo.createNew());

		Convite convite = conviteFactory.createNew(grupo, creator);
		conviteRepository.save(convite);

		Convite byCode = conviteRepository.findByCode(convite.getCode()).orElseThrow();
		Convite byToken = conviteRepository.findByLinkToken(convite.getLinkToken()).orElseThrow();

		assertEquals(convite.getId(), byCode.getId());
		assertEquals(convite.getId(), byToken.getId());
		assertEquals(InviteStatus.ACTIVE, byCode.getStatus());
		assertTrue(byCode.getExpiresAt().isAfter(byCode.getCreatedAt()));
	}
}
