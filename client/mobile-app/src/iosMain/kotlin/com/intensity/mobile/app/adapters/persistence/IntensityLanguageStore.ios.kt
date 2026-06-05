package com.intensity.mobile.app.adapters.persistence

import com.intensity.mobile.app.platform.i18n.AppLanguage
import platform.Foundation.NSUserDefaults

private const val KEY_APP_LANGUAGE = "intensity_app_language"

actual object IntensityLanguageStore {
    actual fun init(platformContext: Any?) {
        // NSUserDefaults does not require explicit initialization.
    }

    actual fun getLanguage(): AppLanguage {
        val raw = NSUserDefaults.standardUserDefaults.stringForKey(KEY_APP_LANGUAGE) ?: AppLanguage.PT.code
        return AppLanguage.fromCode(raw)
    }

    actual fun setLanguage(language: AppLanguage) {
        NSUserDefaults.standardUserDefaults.setObject(language.code, KEY_APP_LANGUAGE)
    }
}
