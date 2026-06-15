package com.intensity.participante.controller;

import com.intensity.participante.dto.AuthSessionResponse;
import com.intensity.participante.dto.JointLoginRequest;
import com.intensity.participante.dto.LoginRequest;
import com.intensity.participante.dto.RegisterParticipantRequest;
import com.intensity.participante.dto.RegisterParticipantResponse;
import com.intensity.participante.entity.Participante;
import com.intensity.participante.service.ParticipanteService;
import com.intensity.grupo.dto.JointAuthSessionResponse;
import com.intensity.grupo.service.GrupoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class AuthController {

	private final ParticipanteService participanteService;
	private final GrupoService grupoService;

	public AuthController(ParticipanteService participanteService, GrupoService grupoService) {
		this.participanteService = participanteService;
		this.grupoService = grupoService;
	}

	@PostMapping("/auth/login")
	public AuthSessionResponse login(@Valid @RequestBody LoginRequest request) {
		return participanteService.login(request);
	}

	@PostMapping("/auth/grupo")
	public JointAuthSessionResponse loginExperienceBox(@Valid @RequestBody JointLoginRequest request) {
		List<Participante> participantes = new ArrayList<>();
		for (LoginRequest credential : request.credentials()) {
			participantes.add(participanteService.authenticate(credential));
		}
		return grupoService.openExperienceBoxSession(participantes);
	}
}
