package com.intensity.convite.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class InviteExpirationPolicy {

	public static final Duration VALIDITY = Duration.ofDays(7);

	public Instant expiresAt(Instant createdAt) {
		return createdAt.plus(VALIDITY);
	}
}
