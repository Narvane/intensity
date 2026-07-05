package com.intensity.experience.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ExperienceParametersDto(
		@NotNull @Min(1) @Max(5) Integer effort,
		@NotNull @Min(1) @Max(5) Integer unpredictability,
		@NotNull @Min(1) @Max(5) Integer novelty) {
}
