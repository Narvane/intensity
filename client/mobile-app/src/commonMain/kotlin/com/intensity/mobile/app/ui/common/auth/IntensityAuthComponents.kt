package com.intensity.mobile.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intensity.mobile.app.ui.theme.IntensityBrand

private val TopBarShape = RectangleShape

enum class AuthSegmentVariant {
    Brown,
    Blue
}

@Composable
fun IntensityAuthBrandHeader(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(TopBarShape)
            .background(IntensityBrand.ParticipantBarBrush)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Inventory2,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Intensity",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun IntensityAuthSegmentButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    variant: AuthSegmentVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    val brown = MaterialTheme.colorScheme.primary
    val blue = IntensityBrand.RoleParticipant
    val (bg, fg, border) = when {
        !selected -> Triple(Color.White, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.outline)
        variant == AuthSegmentVariant.Brown -> Triple(brown, Color.White, brown)
        else -> Triple(blue, Color.White, blue)
    }
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = bg,
        border = BorderStroke(2.dp, border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(4.dp))
            Text(
                label,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun IntensityAuthModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    variant: AuthSegmentVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brown = MaterialTheme.colorScheme.primary
    val blue = IntensityBrand.RoleParticipant
    val colors = when {
        !selected -> AuthModeCardColors(
            bg = Color.White,
            titleColor = MaterialTheme.colorScheme.onSurface,
            subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant,
            border = MaterialTheme.colorScheme.outline
        )
        variant == AuthSegmentVariant.Brown -> AuthModeCardColors(
            bg = brown,
            titleColor = Color.White,
            subtitleColor = Color.White.copy(alpha = 0.88f),
            border = brown
        )
        else -> AuthModeCardColors(
            bg = blue,
            titleColor = Color.White,
            subtitleColor = Color.White.copy(alpha = 0.88f),
            border = blue
        )
    }
    Surface(
        modifier = modifier
            .heightIn(min = 76.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = colors.bg,
        border = BorderStroke(2.dp, colors.border),
        tonalElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.titleColor,
                modifier = Modifier.size(30.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.subtitleColor
                )
            }
        }
    }
}

private data class AuthModeCardColors(
    val bg: Color,
    val titleColor: Color,
    val subtitleColor: Color,
    val border: Color
)
