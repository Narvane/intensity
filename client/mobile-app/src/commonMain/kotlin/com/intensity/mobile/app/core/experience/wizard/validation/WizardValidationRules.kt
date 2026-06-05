package com.intensity.mobile.app.core.experience.wizard.validation

import com.intensity.mobile.app.core.experience.wizard.model.CreationStep
import com.intensity.mobile.app.core.experience.wizard.model.WizardState

class WizardValidationRules {
    fun validateNextStep(state: WizardState): String? {
        return when (state.step) {
            CreationStep.Suggestion -> if (state.description.isBlank()) "missing_description" else null
            CreationStep.Validation -> if (state.reflectionJustification.isBlank()) "missing_reflection" else null
            CreationStep.Parametrization -> {
                if (state.effortStars !in 1..5 || state.opennessStars !in 1..5 || state.noveltyStars !in 1..5) {
                    "missing_stars"
                } else {
                    null
                }
            }
            else -> null
        }
    }
}
