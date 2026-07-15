package com.intensity.group.controller;

import com.intensity.platform.common.AuthPrincipal;
import com.intensity.group.dto.CreateGroupRequest;
import com.intensity.group.dto.GroupResponse;
import com.intensity.group.dto.UpdateGroupRequest;
import com.intensity.group.service.GroupQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/groups")
public class GroupController {

	private final GroupQueryService groupQueryService;

	public GroupController(GroupQueryService groupQueryService) {
		this.groupQueryService = groupQueryService;
	}

	@GetMapping
	public List<GroupResponse> listGroups() {
		return groupQueryService.listForPrincipal(AuthPrincipal.requireCurrent());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GroupResponse createGroup(@RequestBody(required = false) CreateGroupRequest request) {
		return groupQueryService.createForPrincipal(AuthPrincipal.requireCurrent(), request);
	}

	@PatchMapping("/{groupId}")
	public GroupResponse updateGroup(
			@PathVariable UUID groupId, @RequestBody UpdateGroupRequest request) {
		return groupQueryService.updateForPrincipal(AuthPrincipal.requireCurrent(), groupId, request);
	}
}
