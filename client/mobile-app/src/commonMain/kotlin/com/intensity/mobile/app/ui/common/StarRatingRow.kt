package com.intensity.mobile.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.theme.IntensityBrand

@Composable
fun StarRatingRow(
    label: String,
    criterionHelp: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    levelDescription: (Int) -> String,
    leadingIcon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(t(label), style = MaterialTheme.typography.titleSmall)
        }
        Text(t(criterionHelp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (star in 1..5) {
                    IconButton(onClick = { onValueChange(star) }) {
                        Icon(
                            imageVector = if (value > 0 && star <= value) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "$star ${t("common.stars")}",
                            tint = if (star <= value) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
        Text(
            text = if (value in 1..5) t(levelDescription(value)) else t("rating.prompt.tap_stars"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Label e estrelas empilhados e centralizados (carta avirada para baixoa). */
@Composable
fun StarRatingReadOnlyCentered(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            for (star in 1..5) {
                Icon(
                    imageVector = if (star <= value) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (star <= value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun StarRatingReadOnlyRow(
    label: String,
    value: Int,
    leadingIcon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    starTint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            for (star in 1..5) {
                Icon(
                    imageVector = if (star <= value) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (star <= value) starTint else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

val EffortParamIcon: ImageVector = Icons.Filled.Bolt
val OpennessParamIcon: ImageVector = Icons.Filled.People
val NoveltyParamIcon: ImageVector = Icons.Filled.Lightbulb
val EffortParamColor: Color = IntensityBrand.ParamEffort
val OpennessParamColor: Color = IntensityBrand.ParamDiscomfort
val NoveltyParamColor: Color = IntensityBrand.ParamDaring
