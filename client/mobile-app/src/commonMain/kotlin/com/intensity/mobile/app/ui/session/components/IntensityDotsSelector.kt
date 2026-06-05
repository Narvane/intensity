package com.intensity.mobile.app.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.core.session.model.ExperienceFilter
import com.intensity.mobile.app.ui.theme.IntensityBrand

@Composable
fun IntensityDotsRow(
    filter: ExperienceFilter,
    level: Int,
    onChange: (Int) -> Unit
) {
    val accentOrange = Color(0xFFF9A825)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Bolt,
            null,
            tint = accentOrange,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(26.dp)
        )
        for (i in 1..5) {
            val filled = when (filter) {
                ExperienceFilter.FIXED_INTENSITY -> i == level
                ExperienceFilter.MAX_INTENSITY -> i <= level
                else -> false
            }
            val useOrange = filter == ExperienceFilter.MAX_INTENSITY && filled
            val border = when {
                useOrange -> accentOrange
                filled -> IntensityBrand.RoleParticipant
                else -> MaterialTheme.colorScheme.outline
            }
            val bg = when {
                useOrange -> Color(0x33F9A825)
                filled -> IntensityBrand.RoleParticipantSurface
                else -> Color.White
            }
            val fg = when {
                useOrange -> Color(0xFFB45309)
                filled -> IntensityBrand.RoleParticipant
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(40.dp)
                    .background(bg, CircleShape)
                    .border(2.dp, border, CircleShape)
                    .clickable { onChange(i) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = i.toString(), fontWeight = FontWeight.Black, color = fg)
            }
        }
    }
}
