package com.intensity.mobile.app.ui.experience

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.contracts.model.ExperienceDto
import com.intensity.contracts.model.ExperienceSummaryDto
import com.intensity.mobile.app.shell.session.AppSession
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.theme.IntensityBrand
import kotlinx.coroutines.launch

@Composable
fun ExperienceSummaryCard(
    summary: ExperienceSummaryDto,
    myUserId: String?,
    gateway: IntensityGateway,
    session: AppSession,
    onChanged: () -> Unit,
    snackbars: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    var revealed by remember(summary.id) { mutableStateOf(false) }
    var detail by remember(summary.id) { mutableStateOf<ExperienceDto?>(null) }
    val isMine = myUserId != null && summary.createdBy == myUserId
    val revealErrorText = t("experience.card.error_reveal")
    val removedText = t("experience.card.removed")
    val errorPrefixText = t("common.errorPrefix")

    IntensityCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Security, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(
                        t("experience.card.title"),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE8E0D8),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (!revealed) {
                                scope.launch {
                                    runCatching { gateway.getExperience(session.token, summary.id) }
                                        .onSuccess {
                                            detail = it
                                            revealed = true
                                        }
                                        .onFailure {
                                            snackbars.showSnackbar(it.message ?: revealErrorText)
                                        }
                                }
                            } else {
                                revealed = false
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (revealed) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                    }
                }
            }
            if (!isMine) {
                Text(
                    t("experience.card.other_author"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (revealed && detail != null) {
                ExperienceRevealCard(
                    cardKey = summary.id,
                    description = detail!!.description,
                    intensity = detail!!.intensity,
                    parameters = detail!!.parameters,
                    additionalInfo = detail!!.additionalInfo,
                    modifier = Modifier.fillMaxWidth(),
                    coverSideFirst = false
                )
            } else {
                Text(
                    t("experience.card.reveal_hint"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IntensityBadge(summary.intensity)
                TextButton(
                    onClick = {
                        scope.launch {
                            runCatching { gateway.deleteExperience(session.token, summary.id) }
                                .onSuccess {
                                    onChanged()
                                    snackbars.showSnackbar(removedText)
                                }
                                .onFailure { snackbars.showSnackbar("$errorPrefixText: ${it.message}") }
                        }
                    }
                ) {
                    Icon(Icons.Filled.Delete, null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                    Text(t("experience.card.delete"), color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            Text(
                text = "${t("experience.card.seal")}: ${summary.descriptionMd5}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun IntensityBadge(level: Int) {
    val (bg, fg) = when (level.coerceIn(1, 5)) {
        1 -> IntensityBrand.Intensity1Surface to IntensityBrand.Intensity1
        2 -> IntensityBrand.Intensity2Surface to IntensityBrand.Intensity2
        3 -> IntensityBrand.Intensity3Surface to IntensityBrand.Intensity3
        4 -> IntensityBrand.Intensity4Surface to IntensityBrand.Intensity4
        else -> IntensityBrand.Intensity5Surface to IntensityBrand.Intensity5
    }
    Text(
        text = "${t("common.intensity")} $level",
        style = MaterialTheme.typography.labelLarge,
        color = fg,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(bg, MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
