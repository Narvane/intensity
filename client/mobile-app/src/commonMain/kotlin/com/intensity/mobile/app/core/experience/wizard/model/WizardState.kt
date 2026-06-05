package com.intensity.mobile.app.core.experience.wizard.model

data class WizardState(
    val step: CreationStep = CreationStep.Suggestion,
    val description: String = "",
    val reflectionJustification: String = "",
    val effortStars: Int = 0,
    val opennessStars: Int = 0,
    val noveltyStars: Int = 0,
    val selectedIntensity: Int = 3,
    val intensityManualOverride: Boolean = false
)
