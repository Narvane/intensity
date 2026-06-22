package com.intensity.box.controller;

import com.intensity.box.dto.BoxResponse;
import com.intensity.box.dto.CreateBoxRequest;
import com.intensity.box.service.BoxService;
import com.intensity.common.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class BoxController {

	private final BoxService boxService;

	public BoxController(BoxService boxService) {
		this.boxService = boxService;
	}

	@GetMapping("/groups/{groupId}/boxes")
	public List<BoxResponse> listGroupBoxes(@PathVariable UUID groupId) {
		return boxService.listByGroup(groupId, AuthPrincipal.requireCurrent());
	}

	@PostMapping("/boxes")
	@ResponseStatus(HttpStatus.CREATED)
	public BoxResponse createBox(@Valid @RequestBody CreateBoxRequest request) {
		return boxService.create(request, AuthPrincipal.requireCurrent());
	}

	@DeleteMapping("/boxes/{boxId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBox(@PathVariable UUID boxId) {
		boxService.delete(boxId, AuthPrincipal.requireCurrent());
	}
}
