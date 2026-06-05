package com.intensity.mobile.app.core.experience.wizard.port

import com.intensity.contracts.model.ExperienceDto
import com.intensity.contracts.model.ExperienceUpsertRequestDto

interface ExperienceCommandPort {
    suspend fun createExperience(token: String, request: ExperienceUpsertRequestDto): ExperienceDto
}
