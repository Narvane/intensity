package com.intensity.demo;

import com.intensity.participant.repository.ParticipantRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DemoSeedIntegrationTest {

	@Autowired
	private Flyway flyway;

	@Autowired
	private DemoSeedService demoSeedService;

	@Autowired
	private ParticipantRepository participantRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void resetDatabase() {
		flyway.clean();
		flyway.migrate();
	}

	@Test
	void seedCreatesCoupleAndTrioWorld() {
		assertThat(demoSeedService.seedIfEmpty()).isTrue();
		assertThat(demoSeedService.seedIfEmpty()).isFalse();

		assertThat(participantRepository.existsByEmailIgnoreCase(DemoSeedService.LEO_EMAIL)).isTrue();
		assertThat(participantRepository.existsByEmailIgnoreCase(DemoSeedService.MAYA_EMAIL)).isTrue();
		assertThat(participantRepository.existsByEmailIgnoreCase(DemoSeedService.NICO_EMAIL)).isTrue();

		var leo = participantRepository.findByEmailIgnoreCase(DemoSeedService.LEO_EMAIL).orElseThrow();
		assertThat(passwordEncoder.matches(DemoSeedService.DEMO_PASSWORD, leo.getPasswordHash())).isTrue();

		DemoSeedService.SeedSnapshot snapshot = demoSeedService.snapshot();
		assertThat(snapshot.participantCount()).isEqualTo(3);
		assertThat(snapshot.coupleBoxCount()).isEqualTo(3);
		assertThat(snapshot.trioBoxCount()).isEqualTo(2);
		assertThat(snapshot.weekendExperienceCount()).isEqualTo(8);
		assertThat(snapshot.tripExperienceCount()).isEqualTo(6);
		assertThat(snapshot.invitePresent()).isTrue();
	}
}
