package com.intensity.participant.dto;

import com.intensity.common.AccessMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterParticipantRequest(
		@NotBlank @Size(min = 1, max = 80) String displayName,
		@NotBlank @Email String email,
		@NotBlank @Size(min = 8, max = 128) String password) {
}
