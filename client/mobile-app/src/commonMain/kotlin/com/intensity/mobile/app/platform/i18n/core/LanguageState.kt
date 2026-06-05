package com.intensity.mobile.app.platform.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class LanguageState(
    val language: AppLanguage,
    val onLanguageChange: (AppLanguage) -> Unit
)
