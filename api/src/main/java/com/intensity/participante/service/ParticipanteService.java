package com.intensity.participante.service;

import com.intensity.common.AccessMode;
import com.intensity.common.exception.ApiException;
import com.intensity.config.JwtService;
import com.intensity.participante.dto.AuthSessionResponse;
import com.intensity.participante.dto.LoginRequest;
import com.intensity.participante.dto.RegisterParticipantRequest;
import com.intensity.participante.dto.RegisterParticipantResponse;
import com.intensity.participante.entity.Participante;
import com.intensity.participante.repository.AllowlistEmailRepository;
import com.intensity.participante.repository.ParticipanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipanteService {

	private final ParticipanteRepository participanteRepository;
	private final AllowlistEmailRepository allowlistEmailRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public ParticipanteService(
			ParticipanteRepository participanteRepository,
			AllowlistEmailRepository allowlistEmailRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService) {
		this.participanteRepository = participanteRepository;
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

		if (participanteRepository.existsByEmailIgnoreCase(email)) {
			throw new ApiException(
					HttpStatus.CONFLICT,
					"EMAIL_ALREADY_REGISTERED",
					"A participant with this email already exists.");
		}

		Participante participante = new Participante(
				request.displayName().trim(),
				email,
				passwordEncoder.encode(request.password()));

		participanteRepository.save(participante);

		String token = jwtService.createExperiencesToken(participante.getId(), participante.getDisplayName());

		return new RegisterParticipantResponse(
				participante.getId(),
				participante.getDisplayName(),
				participante.getEmail(),
				token);
	}

	public AuthSessionResponse login(LoginRequest request) {
		Participante participante = participanteRepository
				.findByEmailIgnoreCase(request.email().trim())
				.orElseThrow(() -> invalidCredentials());

		if (!passwordEncoder.matches(request.password(), participante.getPasswordHash())) {
			throw invalidCredentials();
		}

		String token = jwtService.createExperiencesToken(participante.getId(), participante.getDisplayName());

		return new AuthSessionResponse(
				token,
				participante.getId(),
				participante.getDisplayName(),
				AccessMode.EXPERIENCES);
	}

	public Participante authenticate(LoginRequest request) {
		Participante participante = participanteRepository
				.findByEmailIgnoreCase(request.email().trim())
				.orElseThrow(this::invalidCredentials);

		if (!passwordEncoder.matches(request.password(), participante.getPasswordHash())) {
			throw invalidCredentials();
		}

		return participante;
	}

	private ApiException invalidCredentials() {
		return new ApiException(
				HttpStatus.UNAUTHORIZED,
				"INVALID_CREDENTIALS",
				"Invalid email or password.");
	}
}
