package com.intensity.mobile.app.adapters.persistence

/**
 * Persists whether the user has completed the first-run manual (intro).
 * [init] must be called once on Android before other methods (pass Activity/Application context).
 */
expect object IntensityOnboardingStore {
    fun init(platformContext: Any?)

    fun hasSeenIntroManual(): Boolean

    fun markIntroManualSeen()
}
