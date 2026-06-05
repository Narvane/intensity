package com.intensity.mobile.app.platform.i18n

import androidx.compose.runtime.staticCompositionLocalOf

val LocalLanguageState = staticCompositionLocalOf<LanguageState> {
    LanguageState(language = AppLanguage.PT, onLanguageChange = {})
}
