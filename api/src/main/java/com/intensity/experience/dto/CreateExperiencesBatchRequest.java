package com.intensity.experience.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateExperiencesBatchRequest(
		@NotEmpty @Size(max = 5) @Valid List<CreateExperienceRequest> experiences) {
}
