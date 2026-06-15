package com.intensity.experiencia.controller;

import com.intensity.common.AuthPrincipal;
import com.intensity.experiencia.dto.CreateExperienceRequest;
import com.intensity.experiencia.dto.ExperienceResponse;
import com.intensity.experiencia.service.ExperienciaService;
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
public class ExperienciaController {

	private final ExperienciaService experienciaService;

	public ExperienciaController(ExperienciaService experienciaService) {
		this.experienciaService = experienciaService;
	}

	@GetMapping("/caixinhas/{boxId}/experiencias")
	public List<ExperienceResponse> listBoxExperiences(@PathVariable UUID boxId) {
		return experienciaService.listByBox(boxId, AuthPrincipal.requireCurrent());
	}

	@PostMapping("/caixinhas/{boxId}/experiencias")
	@ResponseStatus(HttpStatus.CREATED)
	public ExperienceResponse createExperience(
			@PathVariable UUID boxId, @Valid @RequestBody CreateExperienceRequest request) {
		return experienciaService.create(boxId, request, AuthPrincipal.requireCurrent());
	}

	@PutMapping("/experiencias/{experienceId}")
	public ExperienceResponse updateExperience(
			@PathVariable UUID experienceId, @Valid @RequestBody CreateExperienceRequest request) {
		return experienciaService.update(experienceId, request, AuthPrincipal.requireCurrent());
	}

	@DeleteMapping("/experiencias/{experienceId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteExperience(@PathVariable UUID experienceId) {
		experienciaService.delete(experienceId, AuthPrincipal.requireCurrent());
	}
}
