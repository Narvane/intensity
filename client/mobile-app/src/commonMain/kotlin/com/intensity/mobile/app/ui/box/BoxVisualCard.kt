package com.intensity.mobile.app.ui.box

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.ui.theme.IntensityBrand
import androidx.compose.ui.graphics.Color
import com.intensity.mobile.app.ui.theme.IntensityCardContainerDark
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun BoxVisualCard(
    name: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    badgeIcon: ImageVector? = null,
    badgeText: String? = null,
    paletteAccentColor: Color? = null,
    paletteSurfaceColor: Color? = null,
    accentBlue: Boolean = false,
    onClick: () -> Unit = {}
) {
    val dark = isSystemInDarkTheme()
    val container = when {
        dark -> IntensityCardContainerDark
        paletteSurfaceColor != null -> paletteSurfaceColor
        else -> IntensityBrand.RoleParticipantSurface
    }
    val accent = paletteAccentColor ?: IntensityBrand.RoleParticipant
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (paletteAccentColor != null) {
                    Modifier.border(BorderStroke(1.dp, accent.copy(alpha = 0.45f)), RoundedCornerShape(20.dp))
                } else if (accentBlue) {
                    Modifier.border(BorderStroke(1.dp, Color(0x551E5EFF)), RoundedCornerShape(20.dp))
                }
                else Modifier
            )
            .background(container)
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (paletteSurfaceColor != null) paletteSurfaceColor.copy(alpha = 0.6f)
                    else if (accentBlue) IntensityBrand.RoleParticipantSurface
                    else MaterialTheme.colorScheme.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (paletteAccentColor != null) accent
                else if (accentBlue) IntensityBrand.RoleParticipant
                else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (badgeIcon != null && !badgeText.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.75f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = if (paletteAccentColor != null) accent
                    else if (accentBlue) IntensityBrand.RoleParticipant
                    else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
