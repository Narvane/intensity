package com.intensity.mobile.app.platform.i18n

import androidx.compose.runtime.Composable

private val Dictionaries = mapOf(
    AppLanguage.PT to Pt,
    AppLanguage.EN to En,
    AppLanguage.IT to It
)

fun tr(language: AppLanguage, key: String, vararg args: Any?): String {
    val raw = Dictionaries[language]?.get(key)
        ?: Dictionaries[AppLanguage.PT]?.get(key)
        ?: resolveSuggestionByLanguage(language, key)
        ?: resolveSuggestionByLanguage(AppLanguage.PT, key)
        ?: key
    return args.foldIndexed(raw) { index, acc, arg ->
        acc.replace("{$index}", arg?.toString().orEmpty())
    }
}

@Composable
fun t(key: String, vararg args: Any?): String {
    val state = LocalLanguageState.current
    return tr(state.language, key, *args)
}

@Composable
fun ts(portugueseText: String): String {
    val language = LocalLanguageState.current.language
    return ts(language, portugueseText)
}

fun ts(language: AppLanguage, portugueseText: String): String {
    return when (language) {
        AppLanguage.PT -> portugueseText
        AppLanguage.EN -> portugueseText
        AppLanguage.IT -> portugueseText
    }
}

private fun resolveSuggestionByLanguage(language: AppLanguage, key: String): String? {
    return when (language) {
        AppLanguage.PT -> SuggestionPacksPt.resolve(key)
        AppLanguage.EN -> SuggestionPacksPt.resolve(key)
        AppLanguage.IT -> SuggestionPacksPt.resolve(key)
    }
}
