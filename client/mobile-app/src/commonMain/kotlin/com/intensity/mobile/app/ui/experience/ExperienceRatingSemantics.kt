package com.intensity.mobile.app.ui.experience

object ExperienceRatingSemantics {
    const val effortHelp = "rating.help.effort"
    const val opennessHelp = "rating.help.openness"
    const val noveltyHelp = "rating.help.novelty"

    fun effortLevel(stars: Int): String = when (stars) {
        1 -> "rating.level.effort.1"
        2 -> "rating.level.effort.2"
        3 -> "rating.level.effort.3"
        4 -> "rating.level.effort.4"
        5 -> "rating.level.effort.5"
        else -> ""
    }

    fun opennessLevel(stars: Int): String = when (stars) {
        1 -> "rating.level.openness.1"
        2 -> "rating.level.openness.2"
        3 -> "rating.level.openness.3"
        4 -> "rating.level.openness.4"
        5 -> "rating.level.openness.5"
        else -> ""
    }

    fun noveltyLevel(stars: Int): String = when (stars) {
        1 -> "rating.level.novelty.1"
        2 -> "rating.level.novelty.2"
        3 -> "rating.level.novelty.3"
        4 -> "rating.level.novelty.4"
        5 -> "rating.level.novelty.5"
        else -> ""
    }
}
