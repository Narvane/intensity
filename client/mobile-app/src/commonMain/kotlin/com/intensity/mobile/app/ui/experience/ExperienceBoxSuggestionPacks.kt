package com.intensity.mobile.app.ui.experience

import com.intensity.mobile.app.ui.session.ExperienceBoxTypeCode

/**
 * Facade de sugestões por intensidade (1..5) por tipo de caixa.
 * Retorna apenas chaves de i18n.
 */
object ExperienceBoxSuggestionPacks {
    val DEFAULT_TYPE: String = ExperienceBoxTypeCode.DEFAULT.code

    fun normalizeType(code: String?): String {
        return ExperienceBoxTypeCode.fromCode(code).code
    }

    fun byIntensity(boxTypeCode: String?): Map<Int, List<String>> {
        val type = normalizeType(boxTypeCode)
        return (1..5).associateWith { level ->
            (1..3).map { index ->
                "suggestion.$type.$level.$index.text"
            }
        }
    }
}
