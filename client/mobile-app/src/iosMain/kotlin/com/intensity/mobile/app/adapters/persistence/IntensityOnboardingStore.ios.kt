package com.intensity.mobile.app.adapters.persistence

import platform.Foundation.NSUserDefaults

private const val KEY_SEEN_MANUAL = "between_us_seen_intro_manual"

actual object IntensityOnboardingStore {
    actual fun init(platformContext: Any?) {
        // NSUserDefaults does not require init on iOS.
    }

    actual fun hasSeenIntroManual(): Boolean =
        NSUserDefaults.standardUserDefaults.boolForKey(KEY_SEEN_MANUAL)

    actual fun markIntroManualSeen() {
        NSUserDefaults.standardUserDefaults.setBool(true, KEY_SEEN_MANUAL)
    }
}
