package com.intensity.mobile.app.ui.experience

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.session.ExperienceBoxTypeCatalog
import com.intensity.mobile.app.ui.theme.IntensityBrand

private data class IntensityVisual(
    val subtitle: String,
    val accent: Color,
    val surface: Color,
    val icon: ImageVector
)

private fun intensityVisual(level: Int): IntensityVisual = when (level) {
    1 -> IntensityVisual("intensity.subtitle.1", IntensityBrand.Intensity1, IntensityBrand.Intensity1Surface, Icons.Filled.Lightbulb)
    2 -> IntensityVisual("intensity.subtitle.2", IntensityBrand.Intensity2, IntensityBrand.Intensity2Surface, Icons.Filled.People)
    3 -> IntensityVisual("intensity.subtitle.3", IntensityBrand.Intensity3, IntensityBrand.Intensity3Surface, Icons.Filled.Star)
    4 -> IntensityVisual("intensity.subtitle.4", IntensityBrand.Intensity4, IntensityBrand.Intensity4Surface, Icons.Filled.Bolt)
    5 -> IntensityVisual("intensity.subtitle.5", IntensityBrand.Intensity5, IntensityBrand.Intensity5Surface, Icons.Filled.PriorityHigh)
    else -> IntensityVisual("", Color.Gray, Color.LightGray, Icons.Filled.Star)
}

@Composable
fun SuggestionStepContent(
    experienceBoxType: String?,
    onPickSuggestion: (String) -> Unit
) {
    val normalized = ExperienceBoxSuggestionPacks.normalizeType(experienceBoxType)
    val normalizedLabel = ExperienceBoxTypeCatalog.shortLabel(normalized)
    Text(
        t("wizard.suggestion.hint"),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
        "${t("wizard.suggestion.for_type")}: ${t(normalizedLabel)}",
        style = MaterialTheme.typography.labelMedium,
        color = IntensityBrand.RoleParticipant,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.padding(top = 2.dp))
    groupedSuggestionCards(normalized, onPickSuggestion)
}

@Composable
private fun groupedSuggestionCards(normalizedType: String, onPickSuggestion: (String) -> Unit) {
    val grouped = remember(normalizedType) { ExperienceBoxSuggestionPacks.byIntensity(normalizedType) }
    grouped.keys.sorted().forEach { level ->
        val vis = intensityVisual(level)
        IntensityIdeaCard(
            level = level,
            subtitle = vis.subtitle,
            accentColor = vis.accent,
            surfaceColor = vis.surface,
            icon = vis.icon,
            examples = grouped[level].orEmpty(),
            onPick = onPickSuggestion
        )
    }
}

@Composable
private fun IntensityIdeaCard(
    level: Int,
    subtitle: String,
    accentColor: Color,
    surfaceColor: Color,
    icon: ImageVector,
    examples: List<String>,
    onPick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        color = surfaceColor,
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.45f)),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text("${t("common.intensity")} $level", style = MaterialTheme.typography.labelMedium, color = accentColor, fontWeight = FontWeight.Bold)
                    Text(t(subtitle), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            examples.forEach { line ->
                val localizedLine = t(line)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onPick(localizedLine) }
                        .background(Color.White.copy(alpha = 0.55f))
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("-", color = accentColor, style = MaterialTheme.typography.titleMedium)
                    Text(localizedLine, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
