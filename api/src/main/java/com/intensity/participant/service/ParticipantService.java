package com.intensity.participant.service;

import com.intensity.platform.common.AccessMode;
import com.intensity.platform.common.exception.ApiException;
import com.intensity.platform.security.JwtService;
import com.intensity.participant.RegistrationProperties;
import com.intensity.participant.dto.AuthSessionResponse;
import com.intensity.participant.dto.LoginRequest;
import com.intensity.participant.dto.RegisterParticipantRequest;
import com.intensity.participant.dto.RegisterParticipantResponse;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.AllowlistEmailRepository;
import com.intensity.participant.repository.ParticipantRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ParticipantService {

	private final ParticipantRepository participantRepository;
	private final AllowlistEmailRepository allowlistEmailRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RegistrationProperties registrationProperties;

	public ParticipantService(
			ParticipantRepository participantRepository,
			AllowlistEmailRepository allowlistEmailRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			RegistrationProperties registrationProperties) {
		this.participantRepository = participantRepository;
		this.allowlistEmailRepository = allowlistEmailRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.registrationProperties = registrationProperties;
	}

	@Transactional
	public RegisterParticipantResponse register(RegisterParticipantRequest request) {
		String email = request.email().trim().toLowerCase();

		if (registrationProperties.allowlistEnabled() && !allowlistEmailRepository.existsById(email)) {
			throw new ApiException(
					HttpStatus.FORBIDDEN,
					"EMAIL_NOT_ALLOWLISTED",
					"This email is not authorized to register.");
		}

		if (participantRepository.existsByEmailIgnoreCase(email)) {
			throw new ApiException(
					HttpStatus.CONFLICT,
					"EMAIL_ALREADY_REGISTERED",
					"A participant with this email already exists.");
		}

		Participant participant = new Participant(
				request.displayName().trim(),
				email,
				passwordEncoder.encode(request.password()));

		participantRepository.save(participant);

		String token = jwtService.createExperiencesToken(participant.getId(), participant.getDisplayName());

		return new RegisterParticipantResponse(
				participant.getId(),
				participant.getDisplayName(),
				participant.getEmail(),
				token);
	}

	public AuthSessionResponse login(LoginRequest request) {
		Participant participant = participantRepository
				.findByEmailIgnoreCase(request.email().trim())
				.orElseThrow(() -> invalidCredentials());

		if (!passwordEncoder.matches(request.password(), participant.getPasswordHash())) {
			throw invalidCredentials();
		}

		String token = jwtService.createExperiencesToken(participant.getId(), participant.getDisplayName());

		return new AuthSessionResponse(
				token,
				participant.getId(),
				participant.getDisplayName(),
				AccessMode.EXPERIENCES);
	}

	public Participant authenticate(LoginRequest request) {
		Participant participant = participantRepository
				.findByEmailIgnoreCase(request.email().trim())
				.orElseThrow(this::invalidCredentials);

		if (!passwordEncoder.matches(request.password(), participant.getPasswordHash())) {
			throw invalidCredentials();
		}

		return participant;
	}

	public Participant requireFromExperiencesToken(String token) {
		Claims claims;
		try {
			claims = jwtService.parse(token);
		} catch (RuntimeException exception) {
			throw JwtService.unauthorized();
		}

		AccessMode mode = AccessMode.valueOf(claims.get("accessMode", String.class));
		if (mode != AccessMode.EXPERIENCES) {
			throw JwtService.unauthorized();
		}

		UUID participantId = UUID.fromString(claims.getSubject());
		return participantRepository
				.findById(participantId)
				.orElseThrow(JwtService::unauthorized);
	}

	private ApiException invalidCredentials() {
		return new ApiException(
				HttpStatus.UNAUTHORIZED,
				"INVALID_CREDENTIALS",
				"Invalid email or password.");
	}
}
