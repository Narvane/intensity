package com.intensity.mobile.app.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.contracts.model.ExperienceSummaryDto
import com.intensity.mobile.app.core.experience.selection.DrawExperienceUseCase
import com.intensity.mobile.app.core.session.model.ExperienceFilter
import com.intensity.mobile.app.shell.session.AppSession
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.IntensityExperienceFilterChip
import com.intensity.mobile.app.ui.common.IntensityGradientTopBar
import com.intensity.mobile.app.ui.common.IntensityPrimaryBlueButton
import com.intensity.mobile.app.ui.common.IntensityPrimaryBrownButton
import com.intensity.mobile.app.ui.common.IntensityTopBarLogoutRow
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import com.intensity.mobile.app.ui.experience.ExperienceRevealCard
import com.intensity.mobile.app.ui.theme.IntensityBrand
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedMomentScreen(
    gateway: IntensityGateway,
    session: AppSession,
    boxId: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawExperienceUseCase = remember { DrawExperienceUseCase() }
    val snackbars = remember { SnackbarHostState() }
    var experienceFilter by remember { mutableStateOf(ExperienceFilter.ANY) }
    var experienceIntensityLevel by remember { mutableStateOf(3) }
    var drawing by remember { mutableStateOf(false) }
    var drawn by remember { mutableStateOf<com.intensity.contracts.model.ExperienceDto?>(null) }
    var descriptionRevealed by remember { mutableStateOf(false) }
    var revealFlipSignal by remember { mutableStateOf(0) }
    var topBarBrush by remember { mutableStateOf(IntensityBrand.ParticipantBarBrush) }
    val errorPrefixText = t("common.errorPrefix")
    val noCandidatesText = t("session.shared.no_candidates")
    val unknownErrorText = t("common.error_unknown")
    val hasDrawnCard = drawn != null

    LaunchedEffect(session.token, boxId) {
        runCatching { gateway.listBoxes(session.token) }
            .onSuccess { boxes ->
                val boxType = boxes.find { it.id == boxId }?.boxType ?: ExperienceBoxTypeCode.DEFAULT.code
                topBarBrush = ExperienceBoxTypeCatalog.topBarBrushFor(boxType)
            }
    }

    LaunchedEffect(drawn?.id) { descriptionRevealed = false }

    val sortLabel = when (experienceFilter) {
        ExperienceFilter.ANY -> if (drawing) t("session.shared.drawing") else t("session.shared.activate_experience")
        ExperienceFilter.FIXED_INTENSITY -> if (drawing) t("session.shared.drawing") else "${t("session.shared.activate_fixed_intensity")} $experienceIntensityLevel"
        ExperienceFilter.MAX_INTENSITY -> if (drawing) t("session.shared.drawing") else "${t("session.shared.activate_max_intensity")} $experienceIntensityLevel"
    }

    Scaffold(
        topBar = {
            IntensityGradientTopBar(
                title = t("session.shared.draw_experience"),
                backgroundBrush = topBarBrush,
                navigation = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onBack)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = t("session.boxes.title_short"),
                            modifier = Modifier.size(22.dp),
                            tint = Color.White
                        )
                        Text(t("session.boxes.title_short"), color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 2.dp))
                    }
                },
                actions = { IntensityTopBarLogoutRow(onLogout) }
            )
        },
        snackbarHost = { SnackbarHost(snackbars) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(intensityScreenBackdropBrush())
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IntensityCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Favorite, null, tint = IntensityBrand.RoleParticipant, modifier = Modifier.size(24.dp))
                        Text(
                            t("session.shared.activate_experience"),
                            style = MaterialTheme.typography.titleMedium,
                            color = IntensityBrand.RoleParticipant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IntensityExperienceFilterChip(
                            label = t("session.shared.filter.any"),
                            icon = Icons.Filled.Shuffle,
                            selected = experienceFilter == ExperienceFilter.ANY,
                            onClick = { if (!hasDrawnCard) experienceFilter = ExperienceFilter.ANY },
                            modifier = Modifier.weight(1f)
                        )
                        IntensityExperienceFilterChip(
                            label = t("session.shared.filter.fixed"),
                            icon = Icons.Filled.MyLocation,
                            selected = experienceFilter == ExperienceFilter.FIXED_INTENSITY,
                            onClick = { if (!hasDrawnCard) experienceFilter = ExperienceFilter.FIXED_INTENSITY },
                            modifier = Modifier.weight(1f)
                        )
                        IntensityExperienceFilterChip(
                            label = t("session.shared.filter.max"),
                            icon = Icons.Filled.TrendingDown,
                            selected = experienceFilter == ExperienceFilter.MAX_INTENSITY,
                            onClick = { if (!hasDrawnCard) experienceFilter = ExperienceFilter.MAX_INTENSITY },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (experienceFilter != ExperienceFilter.ANY) {
                        Text(
                            if (experienceFilter == ExperienceFilter.FIXED_INTENSITY) t("session.shared.hint.fixed_level")
                            else t("session.shared.hint.max_level"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IntensityDotsRow(
                            filter = experienceFilter,
                            level = experienceIntensityLevel,
                            onChange = { if (!hasDrawnCard) experienceIntensityLevel = it }
                        )
                    }
                    IntensityPrimaryBrownButton(
                        text = sortLabel,
                        enabled = !drawing && !hasDrawnCard,
                        leadingIcon = Icons.Filled.Favorite,
                        onClick = {
                            drawing = true
                            scope.launch {
                                val fixed = if (experienceFilter == ExperienceFilter.FIXED_INTENSITY) experienceIntensityLevel else null
                                val max = if (experienceFilter == ExperienceFilter.MAX_INTENSITY) experienceIntensityLevel else null
                                runCatching {
                                    val candidates: List<ExperienceSummaryDto> = gateway.listConnectExperiences(
                                        token = session.token,
                                        boxId = boxId,
                                        intensity = fixed,
                                        maxIntensity = max
                                    )
                                    val selected = drawExperienceUseCase.execute(candidates) ?: error("no_candidates")
                                    gateway.getConnectExperience(session.token, boxId, selected.id)
                                }.onSuccess {
                                    drawn = it
                                }.onFailure {
                                    val reason = if (it.message == "no_candidates") noCandidatesText else (it.message ?: unknownErrorText)
                                    snackbars.showSnackbar("$errorPrefixText: $reason")
                                }
                                drawing = false
                            }
                        }
                    )
                }
            }

            val experience = drawn
            if (experience != null) {
                Text(
                    text = "${t("experience.card.seal")}: ${experience.descriptionMd5}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                ExperienceRevealCard(
                    cardKey = experience.id,
                    description = experience.description,
                    intensity = experience.intensity,
                    parameters = experience.parameters,
                    additionalInfo = experience.additionalInfo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x08F9A825))
                        .padding(8.dp),
                    coverSideFirst = true,
                    onDescriptionFaceChanged = { descriptionRevealed = it },
                    showFlipButton = false,
                    externalFlipSignal = revealFlipSignal,
                    emphasizedCover = true,
                    useInnerCardContainer = false
                )
                if (!descriptionRevealed) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x08F9A825))
                            .drawBehind {
                                drawRoundRect(
                                    color = Color(0x99F59E42),
                                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                                    style = Stroke(
                                        width = 2.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                                    )
                                )
                            }
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Shuffle, null, tint = Color(0xFFB45309), modifier = Modifier.size(22.dp))
                            Text(
                                t("session.shared.reveal_hint"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    IntensityPrimaryBlueButton(
                        text = t("experienceCard.flip.showDescription"),
                        enabled = true,
                        leadingIcon = Icons.Filled.FlipToFront,
                        onClick = { revealFlipSignal++ }
                    )
                }
                IntensityPrimaryBrownButton(
                    text = t("session.shared.back_to_draw"),
                    enabled = true,
                    leadingIcon = Icons.Filled.Shuffle,
                    onClick = {
                        drawn = null
                        descriptionRevealed = false
                        revealFlipSignal = 0
                    }
                )
            } else {
                IntensityCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = IntensityBrand.RoleParticipant, modifier = Modifier.size(22.dp))
                            Text(t("session.shared.draw_to_see_intensity"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            t("session.shared.empty_hint"),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
