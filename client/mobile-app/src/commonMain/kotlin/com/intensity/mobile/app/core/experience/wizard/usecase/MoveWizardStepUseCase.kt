package com.intensity.mobile.app.core.experience.wizard.usecase

import com.intensity.mobile.app.core.experience.wizard.model.CreationStep

class MoveWizardStepUseCase {
    fun next(step: CreationStep): CreationStep {
        return when (step) {
            CreationStep.Suggestion -> CreationStep.Validation
            CreationStep.Validation -> CreationStep.Parametrization
            CreationStep.Parametrization -> CreationStep.Classification
            CreationStep.Classification -> CreationStep.Bifurcation
            CreationStep.Bifurcation -> CreationStep.Bifurcation
        }
    }

    fun previous(step: CreationStep): CreationStep {
        return when (step) {
            CreationStep.Validation -> CreationStep.Suggestion
            CreationStep.Parametrization -> CreationStep.Validation
            CreationStep.Classification -> CreationStep.Parametrization
            CreationStep.Bifurcation -> CreationStep.Classification
            CreationStep.Suggestion -> CreationStep.Suggestion
        }
    }
}
