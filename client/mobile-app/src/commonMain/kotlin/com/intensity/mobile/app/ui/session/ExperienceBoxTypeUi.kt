package com.intensity.mobile.app.ui.session

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.theme.IntensityBrand

data class ExperienceBoxTypePalette(
    val accent: Color,
    val surface: Color,
    val topBarBrush: Brush
)

data class ExperienceBoxTypeUiOption(
    val code: String,
    val titleKey: String,
    val subtitleKey: String,
    val section: String,
    val icon: ImageVector,
    val palette: ExperienceBoxTypePalette
)

object ExperienceBoxTypeCatalog {
    val options: List<ExperienceBoxTypeUiOption> = listOf(
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.OUTINGS_FRIENDS.code,
            titleKey = "box.type.outings_friends.title",
            subtitleKey = "box.type.outings_friends.subtitle",
            section = "friends",
            icon = Icons.Filled.Groups,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFF1E5EFF),
                surface = Color(0xFFE8F1FF),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFF1E5EFF), Color(0xFF4C7CFF)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.OUTINGS_COUPLE.code,
            titleKey = "box.type.outings_couple.title",
            subtitleKey = "box.type.outings_couple.subtitle",
            section = "couple",
            icon = Icons.Filled.Favorite,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFFEA7A12),
                surface = Color(0xFFFFF0E1),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFFEA7A12), Color(0xFFF39A3E)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.TRIPS_COUPLE.code,
            titleKey = "box.type.trips_couple.title",
            subtitleKey = "box.type.trips_couple.subtitle",
            section = "couple",
            icon = Icons.Filled.Flight,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFF7A3FE0),
                surface = Color(0xFFF1E9FF),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFF7A3FE0), Color(0xFF9666F0)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.INTIMATE_COUPLE.code,
            titleKey = "box.type.intimate_couple.title",
            subtitleKey = "box.type.intimate_couple.subtitle",
            section = "couple",
            icon = Icons.Filled.Spa,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFFD62839),
                surface = Color(0xFFFFE9EC),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFFD62839), Color(0xFFE94B5B)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.TRIPS_FRIENDS.code,
            titleKey = "box.type.trips_friends.title",
            subtitleKey = "box.type.trips_friends.subtitle",
            section = "friends",
            icon = Icons.Filled.Flight,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFF0F8FA5),
                surface = Color(0xFFE4F8FC),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFF0F8FA5), Color(0xFF2AABC0)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.EXPERIENCES_FRIENDS.code,
            titleKey = "box.type.experiences_friends.title",
            subtitleKey = "box.type.experiences_friends.subtitle",
            section = "friends",
            icon = Icons.Filled.Celebration,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFF1E9C49),
                surface = Color(0xFFE8F9EE),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFF1E9C49), Color(0xFF3AB764)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.BREAK_ROUTINE.code,
            titleKey = "box.type.break_routine.title",
            subtitleKey = "box.type.break_routine.subtitle",
            section = "personal",
            icon = Icons.Filled.Lightbulb,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFF2F9A3C),
                surface = Color(0xFFEAF9E8),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFF2F9A3C), Color(0xFF57B462)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.FIRST_TIMES.code,
            titleKey = "box.type.first_times.title",
            subtitleKey = "box.type.first_times.subtitle",
            section = "personal",
            icon = Icons.Filled.Explore,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFF6B49E7),
                surface = Color(0xFFEEE8FF),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFF6B49E7), Color(0xFF8B6AF1)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.LIGHT_DISCOMFORT.code,
            titleKey = "box.type.light_discomfort.title",
            subtitleKey = "box.type.light_discomfort.subtitle",
            section = "personal",
            icon = Icons.Filled.SelfImprovement,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFFE19C12),
                surface = Color(0xFFFFF4DE),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFFE19C12), Color(0xFFEBB33E)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.CONNECTION_MOMENTS.code,
            titleKey = "box.type.connection_moments.title",
            subtitleKey = "box.type.connection_moments.subtitle",
            section = "social",
            icon = Icons.Filled.Park,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFF128C68),
                surface = Color(0xFFE3F7EF),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFF128C68), Color(0xFF33A784)))
            )
        ),
        ExperienceBoxTypeUiOption(
            code = ExperienceBoxTypeCode.DIFFERENT_EXPERIENCES.code,
            titleKey = "box.type.different_experiences.title",
            subtitleKey = "box.type.different_experiences.subtitle",
            section = "social",
            icon = Icons.Filled.Nightlife,
            palette = ExperienceBoxTypePalette(
                accent = Color(0xFFB63FC9),
                surface = Color(0xFFF9E8FD),
                topBarBrush = Brush.horizontalGradient(listOf(Color(0xFFB63FC9), Color(0xFFCC65DB)))
            )
        )
    )

    fun shortLabel(code: String): String =
        options.find { it.code == code }?.titleKey ?: "box.type.outings_friends.title"

    fun optionFor(code: String): ExperienceBoxTypeUiOption? =
        options.find { it.code == code }

    fun topBarBrushFor(code: String): Brush =
        optionFor(code)?.palette?.topBarBrush ?: IntensityBrand.ParticipantBarBrush
}

@Composable
fun ExperienceBoxTypeOptionCard(
    option: ExperienceBoxTypeUiOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val border = if (selected) option.palette.accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    val bg = if (selected) option.palette.surface else option.palette.surface.copy(alpha = 0.5f)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = bg,
        border = BorderStroke(2.dp, border),
        tonalElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = option.palette.accent,
                modifier = Modifier.size(32.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = t(option.titleKey),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = t(option.subtitleKey),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
