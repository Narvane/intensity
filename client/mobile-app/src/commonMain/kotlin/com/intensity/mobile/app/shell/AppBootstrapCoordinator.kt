package com.intensity.mobile.app.shell

import com.intensity.mobile.app.adapters.persistence.IntensityLanguageStore
import com.intensity.mobile.app.adapters.persistence.IntensityOnboardingStore

class AppBootstrapCoordinator {
    suspend fun loadBootstrapState(): BootstrapState {
        val language = IntensityLanguageStore.getLanguage()
        val needsOnboarding = !IntensityOnboardingStore.hasSeenIntroManual()
        return BootstrapState(
            languageReady = true,
            selectedLanguage = language,
            onboardingPrefsReady = true,
            needsFirstRunOnboarding = needsOnboarding
        )
    }
}
