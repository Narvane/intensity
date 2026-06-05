package com.intensity.mobile.app.ui.session

enum class ExperienceBoxTypeCode(val code: String) {
    OUTINGS_FRIENDS("outings_friends"),
    OUTINGS_COUPLE("outings_couple"),
    TRIPS_FRIENDS("trips_friends"),
    TRIPS_COUPLE("trips_couple"),
    INTIMATE_COUPLE("intimate_couple"),
    EXPERIENCES_FRIENDS("experiences_friends"),
    BREAK_ROUTINE("break_routine"),
    FIRST_TIMES("first_times"),
    LIGHT_DISCOMFORT("light_discomfort"),
    CONNECTION_MOMENTS("connection_moments"),
    DIFFERENT_EXPERIENCES("different_experiences");

    companion object {
        val DEFAULT: ExperienceBoxTypeCode = OUTINGS_FRIENDS
        val ALL_CODES: Set<String> = entries.mapTo(linkedSetOf()) { it.code }

        fun fromCode(raw: String?): ExperienceBoxTypeCode {
            val normalized = raw?.trim()?.lowercase().orEmpty()
            return entries.firstOrNull { it.code == normalized } ?: DEFAULT
        }
    }
}
