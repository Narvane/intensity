package com.intensity.grupo.controller;

import com.intensity.common.AuthPrincipal;
import com.intensity.grupo.service.GrupoMembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/grupos/{groupId}/membros")
public class GrupoMemberController {

	private final GrupoMembershipService grupoMembershipService;

	public GrupoMemberController(GrupoMembershipService grupoMembershipService) {
		this.grupoMembershipService = grupoMembershipService;
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void leaveGroup(@PathVariable UUID groupId) {
		grupoMembershipService.leave(groupId, AuthPrincipal.requireCurrent());
	}
}
