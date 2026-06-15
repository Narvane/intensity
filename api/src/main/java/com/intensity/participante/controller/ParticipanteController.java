package com.intensity.participante.controller;

import com.intensity.participante.dto.RegisterParticipantRequest;
import com.intensity.participante.dto.RegisterParticipantResponse;
import com.intensity.participante.service.ParticipanteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/participantes")
public class ParticipanteController {

	private final ParticipanteService participanteService;

	public ParticipanteController(ParticipanteService participanteService) {
		this.participanteService = participanteService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RegisterParticipantResponse register(@Valid @RequestBody RegisterParticipantRequest request) {
		return participanteService.register(request);
	}
}
