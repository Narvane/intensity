package com.intensity.experience.controller;

import com.intensity.common.AuthPrincipal;
import com.intensity.experience.dto.CreateExperienceRequest;
import com.intensity.experience.dto.ExperienceResponse;
import com.intensity.experience.service.ExperienceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class ExperienceController {

	private final ExperienceService experienceService;

	public ExperienceController(ExperienceService experienceService) {
		this.experienceService = experienceService;
	}

	@GetMapping("/boxes/{boxId}/experiences")
	public List<ExperienceResponse> listBoxExperiences(@PathVariable UUID boxId) {
		return experienceService.listByBox(boxId, AuthPrincipal.requireCurrent());
	}

	@PostMapping("/boxes/{boxId}/experiences")
	@ResponseStatus(HttpStatus.CREATED)
	public ExperienceResponse createExperience(
			@PathVariable UUID boxId, @Valid @RequestBody CreateExperienceRequest request) {
		return experienceService.create(boxId, request, AuthPrincipal.requireCurrent());
	}

	@PutMapping("/experiences/{experienceId}")
	public ExperienceResponse updateExperience(
			@PathVariable UUID experienceId, @Valid @RequestBody CreateExperienceRequest request) {
		return experienceService.update(experienceId, request, AuthPrincipal.requireCurrent());
	}

	@DeleteMapping("/experiences/{experienceId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteExperience(@PathVariable UUID experienceId) {
		experienceService.delete(experienceId, AuthPrincipal.requireCurrent());
	}
}
