package br.com.narvane.intensity.experiencebox.domain

/**
 * Tipos nivel 2 persistidos em [intensity.experience_boxes.box_type].
 * Padrao: [OUTINGS_FRIENDS].
 */
object ExperienceBoxTypeCodes {
    const val OUTINGS_FRIENDS = "outings_friends"
    const val OUTINGS_COUPLE = "outings_couple"
    const val TRIPS_FRIENDS = "trips_friends"
    const val TRIPS_COUPLE = "trips_couple"
    const val INTIMATE_COUPLE = "intimate_couple"
    const val EXPERIENCES_FRIENDS = "experiences_friends"
    const val BREAK_ROUTINE = "break_routine"
    const val FIRST_TIMES = "first_times"
    const val LIGHT_DISCOMFORT = "light_discomfort"
    const val CONNECTION_MOMENTS = "connection_moments"
    const val DIFFERENT_EXPERIENCES = "different_experiences"

    const val DEFAULT = OUTINGS_FRIENDS

    private val ALLOWED = setOf(
        OUTINGS_FRIENDS,
        OUTINGS_COUPLE,
        TRIPS_FRIENDS,
        TRIPS_COUPLE,
        INTIMATE_COUPLE,
        EXPERIENCES_FRIENDS,
        BREAK_ROUTINE,
        FIRST_TIMES,
        LIGHT_DISCOMFORT,
        CONNECTION_MOMENTS,
        DIFFERENT_EXPERIENCES
    )

    fun normalize(raw: String?): String {
        val t = raw?.trim()?.lowercase().orEmpty()
        if (t.isEmpty()) return DEFAULT
        return t.takeIf { it in ALLOWED } ?: DEFAULT
    }
}
