package com.intensity.mobile.app

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.intensity.mobile.app.adapters.persistence.IntensityLanguageStore
import com.intensity.mobile.app.adapters.persistence.IntensityOnboardingStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#1E5EFF")
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        IntensityOnboardingStore.init(this)
        IntensityLanguageStore.init(this)
        setContent {
            MainEntry()
        }
    }
}
