package com.intensity.participant.service;

import com.intensity.common.AccessMode;
import com.intensity.common.exception.ApiException;
import com.intensity.config.JwtService;
import com.intensity.participant.dto.AuthSessionResponse;
import com.intensity.participant.dto.LoginRequest;
import com.intensity.participant.dto.RegisterParticipantRequest;
import com.intensity.participant.dto.RegisterParticipantResponse;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.AllowlistEmailRepository;
import com.intensity.participant.repository.ParticipantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipantService {

	private final ParticipantRepository participantRepository;
	private final AllowlistEmailRepository allowlistEmailRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public ParticipantService(
			ParticipantRepository participantRepository,
			AllowlistEmailRepository allowlistEmailRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService) {
		this.participantRepository = participantRepository;
		this.allowlistEmailRepository = allowlistEmailRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public RegisterParticipantResponse register(RegisterParticipantRequest request) {
		String email = request.email().trim().toLowerCase();

		if (!allowlistEmailRepository.existsById(email)) {
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

	private ApiException invalidCredentials() {
		return new ApiException(
				HttpStatus.UNAUTHORIZED,
				"INVALID_CREDENTIALS",
				"Invalid email or password.");
	}
}
