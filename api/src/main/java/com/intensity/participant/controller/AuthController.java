package com.intensity.participant.controller;

import com.intensity.platform.common.exception.ApiException;
import com.intensity.participant.dto.AuthSessionResponse;
import com.intensity.participant.dto.ForgotPasswordRequest;
import com.intensity.participant.dto.JointLoginRequest;
import com.intensity.participant.dto.LoginRequest;
import com.intensity.participant.dto.ResetPasswordRequest;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.service.ParticipantService;
import com.intensity.participant.service.ParticipantDeletionService;
import com.intensity.participant.service.PasswordResetService;
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
	private final PasswordResetService passwordResetService;
	private final ParticipantDeletionService participantDeletionService;
	private final GroupService groupService;

	public AuthController(
			ParticipantService participantService,
			PasswordResetService passwordResetService,
			ParticipantDeletionService participantDeletionService,
			GroupService groupService) {
		this.participantService = participantService;
		this.passwordResetService = passwordResetService;
		this.participantDeletionService = participantDeletionService;
		this.groupService = groupService;
	}

	@PostMapping("/auth/login")
	public AuthSessionResponse login(@Valid @RequestBody LoginRequest request) {
		return participantService.login(request);
	}

	@PostMapping("/auth/group")
	public JointAuthSessionResponse loginExperienceBox(@Valid @RequestBody JointLoginRequest request) {
		if (!request.hasReuseSessionToken() && !request.hasCredentials()) {
			throw new ApiException(
					HttpStatus.UNPROCESSABLE_ENTITY,
					"VALIDATION_ERROR",
					"Provide credentials or reuse an existing Experiences session.");
		}

		List<Participant> participants = new ArrayList<>();
		if (request.hasReuseSessionToken()) {
			participants.add(participantService.requireFromExperiencesToken(request.reuseSessionToken()));
		}
		for (LoginRequest credential : request.credentials()) {
			participants.add(participantService.authenticate(credential));
		}
		return groupService.openExperienceBoxSession(
				participants,
				request.targetGroupId(),
				request.requiresAllMembers());
	}

	@PostMapping("/auth/forgot-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		passwordResetService.requestReset(request);
	}

	@PostMapping("/auth/reset-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		passwordResetService.resetPassword(request);
	}

	@PostMapping("/auth/delete-account")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAccount(@Valid @RequestBody LoginRequest request) {
		participantDeletionService.deleteAccount(request);
	}
}
