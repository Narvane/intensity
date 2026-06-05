package com.intensity.mobile.app.core.experience.wizard.usecase

import com.intensity.contracts.model.ExperienceUpsertRequestDto
import com.intensity.mobile.app.core.experience.wizard.model.WizardState

class BuildExperienceRequestUseCase {
    fun execute(state: WizardState): ExperienceUpsertRequestDto {
        val reflection = state.reflectionJustification.trim()
        return ExperienceUpsertRequestDto(
            description = state.description.trim(),
            intensity = state.selectedIntensity,
            involvesEveryoneJustification = "",
            othersWouldAcceptJustification = reflection,
            mildDiscomfortJustification = "",
            effortStars = state.effortStars,
            opennessStars = state.opennessStars,
            noveltyStars = state.noveltyStars
        )
    }
}
