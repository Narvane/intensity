package com.intensity.mobile.app.adapters.persistence

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.intensity.mobile.app.platform.i18n.AppLanguage

private const val PREFS = "intensity_app"
private const val KEY_APP_LANGUAGE = "app_language"

@SuppressLint("StaticFieldLeak")
private var languagePrefs: SharedPreferences? = null

actual object IntensityLanguageStore {
    actual fun init(platformContext: Any?) {
        if (platformContext is Context) {
            languagePrefs = platformContext.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
    }

    actual fun getLanguage(): AppLanguage {
        val raw = languagePrefs?.getString(KEY_APP_LANGUAGE, AppLanguage.PT.code) ?: AppLanguage.PT.code
        return AppLanguage.fromCode(raw)
    }

    actual fun setLanguage(language: AppLanguage) {
        languagePrefs?.edit()?.putString(KEY_APP_LANGUAGE, language.code)?.apply()
    }
}
