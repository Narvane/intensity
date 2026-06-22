package com.intensity.experience.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateExperienceRequest(
		@NotBlank @Size(min = 1, max = 1000) String description,
		@NotBlank @Size(min = 1, max = 2000) String reflection,
		@NotNull @Min(1) @Max(5) Integer intensity,
		@NotNull @Valid ExperienceParametersDto parameters) {
}
