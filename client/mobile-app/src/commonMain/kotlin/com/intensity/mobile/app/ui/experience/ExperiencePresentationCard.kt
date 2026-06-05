package com.intensity.mobile.app.ui.experience

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.EffortParamColor
import com.intensity.mobile.app.ui.common.EffortParamIcon
import com.intensity.mobile.app.ui.common.NoveltyParamColor
import com.intensity.mobile.app.ui.common.NoveltyParamIcon
import com.intensity.mobile.app.ui.common.OpennessParamColor
import com.intensity.mobile.app.ui.common.OpennessParamIcon
import com.intensity.mobile.app.ui.common.StarRatingReadOnlyRow
import com.intensity.contracts.model.ExperienceResonanceDto
import com.intensity.contracts.model.ExperienceReflectionDto

/**
 * @param coverSideFirst `true` = comeAa avirado para baixoa (intensidade + estrelas); `false` = comeAa no texto (ex.: lista do autor).
 */
@Composable
fun ExperienceRevealCard(
    cardKey: String,
    description: String,
    intensity: Int,
    parameters: ExperienceResonanceDto?,
    additionalInfo: ExperienceReflectionDto?,
    modifier: Modifier = Modifier,
    coverSideFirst: Boolean,
    onDescriptionFaceChanged: (Boolean) -> Unit = {},
    showFlipButton: Boolean = true,
    externalFlipSignal: Int = 0,
    emphasizedCover: Boolean = false,
    useInnerCardContainer: Boolean = true
) {
    var showingDescriptionFace by remember(cardKey, coverSideFirst) {
        mutableStateOf(!coverSideFirst)
    }
    var consumedFlipSignal by remember(cardKey) { mutableStateOf(externalFlipSignal) }
    LaunchedEffect(externalFlipSignal) {
        if (externalFlipSignal != consumedFlipSignal) {
            showingDescriptionFace = !showingDescriptionFace
            consumedFlipSignal = externalFlipSignal
        }
    }
    LaunchedEffect(showingDescriptionFace) {
        onDescriptionFaceChanged(showingDescriptionFace)
    }
    val rotation by animateFloatAsState(
        targetValue = if (showingDescriptionFace) 180f else 0f,
        animationSpec = tween(320),
        label = "reveal"
    )
    val density = LocalDensity.current

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14f * density.density
                }
        ) {
            if (rotation <= 90f) {
                if (useInnerCardContainer) {
                    IntensityCard {
                        ExperienceCardCoverFace(
                            intensity = intensity,
                            parameters = parameters,
                            emphasizedCover = emphasizedCover,
                            modifier = Modifier.padding(vertical = 24.dp, horizontal = 20.dp)
                        )
                    }
                } else {
                    ExperienceCardCoverFace(
                        intensity = intensity,
                        parameters = parameters,
                        emphasizedCover = emphasizedCover,
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 20.dp)
                    )
                }
            } else {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .graphicsLayer { rotationY = 180f }
                ) {
                    if (useInnerCardContainer) {
                        IntensityCard {
                            ExperienceCardDescriptionFace(
                                cardKey = cardKey,
                                description = description,
                                additionalInfo = additionalInfo,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    } else {
                        ExperienceCardDescriptionFace(
                            cardKey = cardKey,
                            description = description,
                            additionalInfo = additionalInfo,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }
        }
        if (showFlipButton) {
            TextButton(onClick = { showingDescriptionFace = !showingDescriptionFace }) {
                Icon(
                    imageVector = if (showingDescriptionFace) Icons.Filled.FlipToBack else Icons.Filled.FlipToFront,
                    contentDescription = null
                )
                Text(
                    text = if (showingDescriptionFace) t("experienceCard.flip.showIntensity") else t("experienceCard.flip.showDescription"),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ExperienceCardCoverFace(
    intensity: Int,
    parameters: ExperienceResonanceDto?,
    emphasizedCover: Boolean,
    modifier: Modifier = Modifier
) {
    val starTint = if (emphasizedCover) Color(0xFFE2A21D) else MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (emphasizedCover) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(
                        color = Color(0xFFF6E6C8),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "⚡  INTENSIDADE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB86A07)
                )
            }
        }
        Text(
            text = intensity.toString(),
            fontSize = 72.sp,
            fontWeight = FontWeight.Black,
            color = if (emphasizedCover) starTint else MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        if (parameters != null) {
            StarRatingReadOnlyRow(
                label = t("experienceCard.labels.commitment"),
                value = parameters.effortStars,
                leadingIcon = EffortParamIcon,
                iconTint = EffortParamColor,
                starTint = starTint
            )
            StarRatingReadOnlyRow(
                label = t("experienceCard.labels.openness"),
                value = parameters.opennessStars,
                leadingIcon = OpennessParamIcon,
                iconTint = OpennessParamColor,
                starTint = starTint
            )
            StarRatingReadOnlyRow(
                label = t("experienceCard.labels.novelty"),
                value = parameters.noveltyStars,
                leadingIcon = NoveltyParamIcon,
                iconTint = NoveltyParamColor,
                starTint = starTint
            )
        } else {
            Text(
                t("experienceCard.noParameters"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ExperienceCardDescriptionFace(
    cardKey: String,
    description: String,
    additionalInfo: ExperienceReflectionDto?,
    modifier: Modifier = Modifier
) {
    var additionalExpanded by remember(cardKey) { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = if (additionalInfo != null) 40.dp else 0.dp)
            )
            AnimatedVisibility(
                visible = additionalExpanded && additionalInfo != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                additionalInfo?.let { inf ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider()
                        Text(
                            t("experienceCard.additionalInfo.title"),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            t("experienceCard.additionalInfo.subtitle"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val singleReflection = inf.othersWouldAccept.isNotBlank() &&
                            inf.involvesEveryone.isBlank() &&
                            inf.mildDiscomfort.isBlank()
                        if (singleReflection) {
                            ValidationInlineBlock(
                                t("experienceCard.additionalInfo.singleQuestion"),
                                inf.othersWouldAccept
                            )
                        } else {
                            ValidationInlineBlock(t("experienceCard.additionalInfo.involvesEveryone"), inf.involvesEveryone)
                            ValidationInlineBlock(t("experienceCard.additionalInfo.othersAccept"), inf.othersWouldAccept)
                            ValidationInlineBlock(t("experienceCard.additionalInfo.mildDiscomfort"), inf.mildDiscomfort)
                        }
                    }
                }
            }
        }
        if (additionalInfo != null) {
            IconButton(
                onClick = { additionalExpanded = !additionalExpanded },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.PriorityHigh,
                    contentDescription = if (additionalExpanded) {
                        t("experienceCard.additionalInfo.hide")
                    } else {
                        t("experienceCard.additionalInfo.show")
                    },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ValidationInlineBlock(title: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}
