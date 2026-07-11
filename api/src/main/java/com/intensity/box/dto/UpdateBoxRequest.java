package com.intensity.box.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBoxRequest(
		@NotBlank @Size(max = 80) String name,
		Boolean requireAllParticipants) {
}
