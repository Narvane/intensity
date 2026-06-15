package com.intensity.convite.service;

import com.intensity.convite.entity.Convite;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.regex.Pattern;

@Component
public class InviteCodeGenerator {

	public static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	public static final int CODE_LENGTH = 6;
	public static final Pattern CODE_PATTERN = Pattern.compile("^[A-HJ-NP-Z2-9]{6}$");

	private final SecureRandom random = new SecureRandom();

	public String generateCode() {
		StringBuilder builder = new StringBuilder(CODE_LENGTH);
		for (int index = 0; index < CODE_LENGTH; index++) {
			builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}
		return builder.toString();
	}

	public boolean isValidFormat(String code) {
		return code != null && CODE_PATTERN.matcher(Convite.normalizeCode(code)).matches();
	}
}
