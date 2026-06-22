package com.intensity.experience.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class SealService {

	/**
	 * Integrity seal derived from description text only.
	 * Recalculated on create and whenever description changes on update.
	 */
	public String computeFromDescription(String description) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(description.trim().getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash, 0, 4).toUpperCase();
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 not available", exception);
		}
	}
}
