package com.intensity.mobile.app.shell

import com.intensity.mobile.app.platform.i18n.AppLanguage

data class BootstrapState(
    val languageReady: Boolean = false,
    val selectedLanguage: AppLanguage = AppLanguage.PT,
    val onboardingPrefsReady: Boolean = false,
    val needsFirstRunOnboarding: Boolean = false
)
