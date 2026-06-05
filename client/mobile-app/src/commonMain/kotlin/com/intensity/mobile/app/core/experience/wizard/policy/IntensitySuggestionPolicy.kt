package com.intensity.mobile.app.core.experience.wizard.policy

import kotlin.math.roundToInt

class IntensitySuggestionPolicy {
    fun suggest(effort: Int, openness: Int, novelty: Int): Int {
        val avg = (effort + openness + novelty) / 3.0
        return avg.roundToInt().coerceIn(1, 5)
    }
}
