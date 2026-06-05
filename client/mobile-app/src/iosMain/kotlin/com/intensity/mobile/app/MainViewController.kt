package com.intensity.mobile.app

import androidx.compose.ui.window.ComposeUIViewController
import com.intensity.mobile.app.adapters.persistence.IntensityLanguageStore
import com.intensity.mobile.app.adapters.persistence.IntensityOnboardingStore
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    IntensityOnboardingStore.init(null)
    IntensityLanguageStore.init(null)
    return ComposeUIViewController {
        MainEntry()
    }
}
