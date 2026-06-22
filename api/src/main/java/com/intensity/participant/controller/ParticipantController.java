package com.intensity.participant.controller;

import com.intensity.participant.dto.RegisterParticipantRequest;
import com.intensity.participant.dto.RegisterParticipantResponse;
import com.intensity.participant.service.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/participants")
public class ParticipantController {

	private final ParticipantService participantService;

	public ParticipantController(ParticipantService participantService) {
		this.participantService = participantService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RegisterParticipantResponse register(@Valid @RequestBody RegisterParticipantRequest request) {
		return participantService.register(request);
	}
}
