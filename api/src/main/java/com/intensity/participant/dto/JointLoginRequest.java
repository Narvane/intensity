package com.intensity.participant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record JointLoginRequest(@NotEmpty @Size(min = 1) List<@Valid LoginRequest> credentials) {
}
