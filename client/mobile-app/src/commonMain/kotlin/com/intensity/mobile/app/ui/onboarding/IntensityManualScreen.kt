package com.intensity.mobile.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.LanguageSelector
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import com.intensity.mobile.app.ui.theme.IntensityBrand

@Composable
fun IntensityManualScreen(
    isFirstRun: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(intensityScreenBackdropBrush())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        Icons.Outlined.HelpOutline,
                        contentDescription = null,
                        tint = IntensityBrand.RoleParticipant,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = t("manual.title"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                LanguageSelector()
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ManualCard(
                    icon = Icons.Filled.MenuBook,
                    title = t("manual.base.title"),
                    lines = listOf(
                        t("manual.base.1"),
                        t("manual.base.2"),
                        t("manual.base.3"),
                        t("manual.base.4"),
                        t("manual.base.5"),
                        t("manual.base.6"),
                        t("manual.base.7")
                    )
                )

                ManualCard(
                    icon = Icons.Filled.Style,
                    title = t("manual.flow.title"),
                    lines = listOf(
                        t("manual.flow.1"),
                        t("manual.flow.2"),
                        t("manual.flow.3")
                    )
                )

                ManualCard(
                    icon = Icons.Filled.FilterList,
                    title = t("manual.intensity.title"),
                    lines = listOf(
                        t("manual.intensity.1"),
                        t("manual.intensity.2"),
                        t("manual.intensity.3"),
                        t("manual.intensity.4")
                    )
                )

                ManualCard(
                    icon = Icons.Filled.Gavel,
                    title = t("manual.consequence.title"),
                    lines = listOf(
                        t("manual.consequence.1"),
                        t("manual.consequence.2"),
                        t("manual.consequence.3"),
                        t("manual.consequence.4")
                    )
                )

                ManualCard(
                    icon = Icons.Filled.Bolt,
                    title = t("manual.essence.title"),
                    lines = listOf(
                        t("manual.essence.1"),
                        t("manual.essence.2")
                    )
                )

                IntensityCard {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TipsAndUpdates,
                            contentDescription = null,
                            tint = IntensityBrand.RoleParticipant
                        )
                        Text(
                            text = t("manual.tip"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            HorizontalDivider()
            Column(Modifier.padding(16.dp)) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IntensityBrand.RoleParticipant,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        if (isFirstRun) t("manual.start") else t("manual.close"),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualCard(
    icon: ImageVector,
    title: String,
    lines: List<String>
) {
    IntensityCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = IntensityBrand.RoleParticipant,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            lines.forEach { line ->
                Text(
                    text = "• $line",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
