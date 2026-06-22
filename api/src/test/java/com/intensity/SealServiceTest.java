package com.intensity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SealServiceTest {

	private final com.intensity.experience.service.SealService sealService =
			new com.intensity.experience.service.SealService();

	@Test
	void sealIsDeterministicForSameDescription() {
		assertEquals(
				sealService.computeFromDescription("Sunset picnic"),
				sealService.computeFromDescription("Sunset picnic"));
	}

	@Test
	void sealChangesWhenDescriptionChanges() {
		assertNotEquals(
				sealService.computeFromDescription("Sunset picnic"),
				sealService.computeFromDescription("Sunset picnic by the lake"));
	}

	@Test
	void sealIgnoresSurroundingWhitespace() {
		assertEquals(
				sealService.computeFromDescription("Sunset picnic"),
				sealService.computeFromDescription("  Sunset picnic  "));
	}
}
