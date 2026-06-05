package com.intensity.mobile.app.core.experience.selection

import com.intensity.contracts.model.ExperienceSummaryDto
import kotlin.random.Random

class DrawExperienceUseCase {
    fun execute(candidates: List<ExperienceSummaryDto>): ExperienceSummaryDto? {
        if (candidates.isEmpty()) {
            return null
        }
        return candidates[Random.nextInt(candidates.size)]
    }
}
