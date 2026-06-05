package com.intensity.mobile.app.core.experience.wizard.usecase

import com.intensity.contracts.model.ExperienceDto
import com.intensity.contracts.model.ExperienceUpsertRequestDto
import com.intensity.mobile.app.core.experience.wizard.port.ExperienceCommandPort

class SubmitExperienceUseCase(
    private val commandPort: ExperienceCommandPort
) {
    suspend fun execute(token: String, request: ExperienceUpsertRequestDto): ExperienceDto {
        return commandPort.createExperience(token, request)
    }
}
