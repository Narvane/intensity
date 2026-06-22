package com.intensity;

import com.intensity.invite.service.InviteCodeGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InviteCodeGeneratorTest {

	private final InviteCodeGenerator generator = new InviteCodeGenerator();

	@Test
	void generatedCodeMatchesCrockfordFormat() {
		for (int index = 0; index < 20; index++) {
			assertTrue(generator.isValidFormat(generator.generateCode()));
		}
	}

	@Test
	void rejectsAmbiguousCharacters() {
		assertFalse(generator.isValidFormat("AB10CD"));
		assertFalse(generator.isValidFormat("AB12C"));
		assertFalse(generator.isValidFormat("AB12CDE"));
	}

	@Test
	void acceptsLowercaseInputAfterNormalization() {
		assertTrue(generator.isValidFormat("ab23cd"));
	}
}
