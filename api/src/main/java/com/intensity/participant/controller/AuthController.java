package com.intensity.participant.controller;

import com.intensity.participant.dto.AuthSessionResponse;
import com.intensity.participant.dto.JointLoginRequest;
import com.intensity.participant.dto.LoginRequest;
import com.intensity.participant.dto.RegisterParticipantRequest;
import com.intensity.participant.dto.RegisterParticipantResponse;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.service.ParticipantService;
import com.intensity.group.dto.JointAuthSessionResponse;
import com.intensity.group.service.GroupService;
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

	private final ParticipantService participantService;
	private final GroupService groupService;

	public AuthController(ParticipantService participantService, GroupService groupService) {
		this.participantService = participantService;
		this.groupService = groupService;
	}

	@PostMapping("/auth/login")
	public AuthSessionResponse login(@Valid @RequestBody LoginRequest request) {
		return participantService.login(request);
	}

	@PostMapping("/auth/group")
	public JointAuthSessionResponse loginExperienceBox(@Valid @RequestBody JointLoginRequest request) {
		List<Participant> participants = new ArrayList<>();
		for (LoginRequest credential : request.credentials()) {
			participants.add(participantService.authenticate(credential));
		}
		return groupService.openExperienceBoxSession(participants);
	}
}
