package com.intensity.invite.controller;

import com.intensity.platform.common.AuthPrincipal;
import com.intensity.invite.dto.AcceptInviteResponse;
import com.intensity.invite.dto.InvitePreviewResponse;
import com.intensity.invite.dto.InviteResponse;
import com.intensity.invite.service.InviteService;
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
public class InviteController {

	private final InviteService inviteService;

	public InviteController(InviteService inviteService) {
		this.inviteService = inviteService;
	}

	@GetMapping("/groups/{groupId}/invites")
	public List<InviteResponse> listGroupInvites(@PathVariable UUID groupId) {
		return inviteService.listActive(groupId, AuthPrincipal.requireCurrent());
	}

	@PostMapping("/groups/{groupId}/invites")
	@ResponseStatus(HttpStatus.CREATED)
	public InviteResponse createInvite(@PathVariable UUID groupId) {
		return inviteService.create(groupId, AuthPrincipal.requireCurrent());
	}

	@GetMapping("/invites/validate")
	public InvitePreviewResponse validateInvite(
			@RequestParam(required = false) String code,
			@RequestParam(required = false, name = "t") UUID linkToken) {
		return inviteService.validate(code, linkToken);
	}

	@PostMapping("/invites/{inviteId}/accept")
	public AcceptInviteResponse acceptInvite(@PathVariable UUID inviteId) {
		return inviteService.accept(inviteId, AuthPrincipal.requireCurrent());
	}

	@DeleteMapping("/invites/{inviteId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void revokeInvite(@PathVariable UUID inviteId) {
		inviteService.revoke(inviteId, AuthPrincipal.requireCurrent());
	}
}
