package com.intensity.mobile.app.adapters.persistence

import com.intensity.mobile.app.platform.i18n.AppLanguage

expect object IntensityLanguageStore {
    fun init(platformContext: Any?)

    fun getLanguage(): AppLanguage

    fun setLanguage(language: AppLanguage)
}
