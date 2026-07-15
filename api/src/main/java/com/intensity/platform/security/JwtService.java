package com.intensity.platform.security;

import com.intensity.platform.common.AccessMode;
import com.intensity.platform.common.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

	private final JwtProperties properties;
	private final SecretKey secretKey;

	public JwtService(JwtProperties properties) {
		this.properties = properties;
		this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
	}

	public String createExperiencesToken(UUID participantId, String displayName) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(participantId.toString())
				.claim("accessMode", AccessMode.EXPERIENCES.name())
				.claim("displayName", displayName)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(properties.expirationSeconds())))
				.signWith(secretKey)
				.compact();
	}

	public String createExperienceBoxToken(List<UUID> groupIds, List<UUID> participantIds, List<String> displayNames) {
		UUID primaryGroupId = groupIds.getFirst();
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(primaryGroupId.toString())
				.claim("accessMode", AccessMode.EXPERIENCE_BOX.name())
				.claim("groupId", primaryGroupId.toString())
				.claim("groupIds", groupIds.stream().map(UUID::toString).toList())
				.claim("participantIds", participantIds.stream().map(UUID::toString).toList())
				.claim("displayNames", displayNames)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(properties.experienceBoxExpirationSeconds())))
				.signWith(secretKey)
				.compact();
	}

	public Claims parse(String token) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (JwtException | IllegalArgumentException exception) {
			throw unauthorized();
		}
	}

	public static ApiException unauthorized() {
		return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid or expired token.");
	}
}
