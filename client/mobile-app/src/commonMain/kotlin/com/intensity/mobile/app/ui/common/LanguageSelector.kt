package com.intensity.mobile.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.AppLanguage
import com.intensity.mobile.app.platform.i18n.LocalLanguageState

@Composable
fun LanguageSelector(modifier: Modifier = Modifier) {
    val languageState = LocalLanguageState.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppLanguage.entries.forEach { lang ->
            val selected = languageState.language == lang
            val circleBg = when (lang) {
                AppLanguage.PT -> Color(0xFF1FA74F)
                AppLanguage.EN -> Color(0xFF2E6FF2)
                AppLanguage.IT -> Color(0xFF1FA74F)
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        },
                        shape = CircleShape
                    )
                    .background(circleBg)
                    .clickable { languageState.onLanguageChange(lang) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lang.flag,
                    fontSize = 19.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
