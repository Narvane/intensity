package com.intensity.group.controller;

import com.intensity.platform.common.AuthPrincipal;
import com.intensity.group.service.GroupMembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/groups/{groupId}/members")
public class GroupMemberController {

	private final GroupMembershipService groupMembershipService;

	public GroupMemberController(GroupMembershipService groupMembershipService) {
		this.groupMembershipService = groupMembershipService;
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void leaveGroup(@PathVariable UUID groupId) {
		groupMembershipService.leave(groupId, AuthPrincipal.requireCurrent());
	}
}
