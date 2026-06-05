package com.intensity.mobile.app.adapters.persistence

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

private const val PREFS = "between_us_app"
private const val KEY_SEEN_MANUAL = "seen_intro_manual"

@SuppressLint("StaticFieldLeak")
private var prefs: SharedPreferences? = null

actual object IntensityOnboardingStore {
    actual fun init(platformContext: Any?) {
        if (platformContext is Context) {
            prefs = platformContext.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }

    actual fun hasSeenIntroManual(): Boolean =
        prefs?.getBoolean(KEY_SEEN_MANUAL, false) ?: false

    actual fun markIntroManualSeen() {
        prefs?.edit()?.putBoolean(KEY_SEEN_MANUAL, true)?.apply()
    }
}
