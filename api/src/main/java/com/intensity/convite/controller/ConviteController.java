package com.intensity.convite.controller;

import com.intensity.common.AuthPrincipal;
import com.intensity.convite.dto.AcceptInviteResponse;
import com.intensity.convite.dto.InvitePreviewResponse;
import com.intensity.convite.dto.InviteResponse;
import com.intensity.convite.service.ConviteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class ConviteController {

	private final ConviteService conviteService;

	public ConviteController(ConviteService conviteService) {
		this.conviteService = conviteService;
	}

	@GetMapping("/grupos/{groupId}/convites")
	public List<InviteResponse> listGroupInvites(@PathVariable UUID groupId) {
		return conviteService.listActive(groupId, AuthPrincipal.requireCurrent());
	}

	@PostMapping("/grupos/{groupId}/convites")
	@ResponseStatus(HttpStatus.CREATED)
	public InviteResponse createInvite(@PathVariable UUID groupId) {
		return conviteService.create(groupId, AuthPrincipal.requireCurrent());
	}

	@GetMapping("/convites/validar")
	public InvitePreviewResponse validateInvite(
			@RequestParam(required = false) String code,
			@RequestParam(required = false, name = "t") UUID linkToken) {
		return conviteService.validate(code, linkToken);
	}

	@PostMapping("/convites/{inviteId}/aceitar")
	public AcceptInviteResponse acceptInvite(@PathVariable UUID inviteId) {
		return conviteService.accept(inviteId, AuthPrincipal.requireCurrent());
	}

	@DeleteMapping("/convites/{inviteId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void revokeInvite(@PathVariable UUID inviteId) {
		conviteService.revoke(inviteId, AuthPrincipal.requireCurrent());
	}
}
