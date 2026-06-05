package com.intensity.mobile.app.adapters.experience

import com.intensity.contracts.model.ExperienceDto
import com.intensity.contracts.model.ExperienceUpsertRequestDto
import com.intensity.mobile.app.core.experience.wizard.port.ExperienceCommandPort
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway

class ExperienceCommandAdapter(
    private val gateway: IntensityGateway
) : ExperienceCommandPort {
    override suspend fun createExperience(token: String, request: ExperienceUpsertRequestDto): ExperienceDto {
        return gateway.createExperience(token, request)
    }
}
