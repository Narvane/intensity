package com.intensity.mobile.app.platform.i18n

enum class AppLanguage(val code: String, val flag: String) {
    PT("pt", "🇧🇷"),
    EN("en", "🇺🇸"),
    IT("it", "🇮🇹");

    companion object {
        fun fromCode(code: String): AppLanguage = entries.firstOrNull { it.code == code } ?: PT
    }
}
