package com.intensity.mobile.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.LanguageSelector
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import com.intensity.mobile.app.ui.theme.IntensityBrand
import intensity_client2.mobile_app.generated.resources.Res
import intensity_client2.mobile_app.generated.resources.onboarding_step1
import intensity_client2.mobile_app.generated.resources.onboarding_step2
import intensity_client2.mobile_app.generated.resources.onboarding_step3
import intensity_client2.mobile_app.generated.resources.onboarding_step4
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class OnboardingStep(
    val image: DrawableResource,
    val lines: List<String>
)

private val OnboardingSteps = listOf(
    OnboardingStep(
        image = Res.drawable.onboarding_step1,
        lines = listOf(
            "onboarding.step1.l1",
            "onboarding.step1.l2",
            "onboarding.step1.l3",
            "onboarding.step1.l4",
            "onboarding.step1.l5"
        )
    ),
    OnboardingStep(
        image = Res.drawable.onboarding_step2,
        lines = listOf(
            "onboarding.step2.l1",
            "onboarding.step2.l2",
            "onboarding.step2.l3",
            "onboarding.step2.l4",
            "onboarding.step2.l5",
            "onboarding.step2.l6",
            "onboarding.step2.l7"
        )
    ),
    OnboardingStep(
        image = Res.drawable.onboarding_step3,
        lines = listOf(
            "onboarding.step3.l1",
            "onboarding.step3.l2",
            "onboarding.step3.l3",
            "onboarding.step3.l4",
            "onboarding.step3.l5",
            "onboarding.step3.l6"
        )
    ),
    OnboardingStep(
        image = Res.drawable.onboarding_step4,
        lines = listOf(
            "onboarding.step4.l1",
            "onboarding.step4.l2",
            "onboarding.step4.l3"
        )
    )
)

@Composable
fun IntensityOnboardingScreen(
    isFirstRun: Boolean,
    onFinish: () -> Unit,
    onOpenManual: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stepIndex by remember { mutableStateOf(0) }
    val step = OnboardingSteps[stepIndex]
    val isLastStep = stepIndex == OnboardingSteps.lastIndex
    val translatedLines = mutableListOf<String>()
    for (lineKey in step.lines) {
        translatedLines.add(t(lineKey))
    }

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
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = t("onboarding.title"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                LanguageSelector()
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IntensityCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = t("onboarding.stepProgress", stepIndex + 1, OnboardingSteps.size),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.25f)
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    width = 1.dp,
                                    color = IntensityBrand.CardBorderWarm,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(step.image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            text = translatedLines.joinToString("\n"),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(OnboardingSteps.size) { index ->
                        val active = index == stepIndex
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (active) 12.dp else 9.dp)
                                .background(
                                    color = if (active) IntensityBrand.RoleParticipant else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isLastStep) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { stepIndex = (stepIndex - 1).coerceAtLeast(0) },
                            enabled = stepIndex > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(t("onboarding.back"))
                        }
                        Button(
                            onClick = { stepIndex = (stepIndex + 1).coerceAtMost(OnboardingSteps.lastIndex) },
                            modifier = Modifier.weight(1.4f),
                            colors = ButtonDefaults.buttonColors(containerColor = IntensityBrand.RoleParticipant)
                        ) {
                            Text(t("onboarding.next"), fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onOpenManual,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(t("onboarding.openManual"), fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = IntensityBrand.RoleParticipant)
                    ) {
                        Text(t("onboarding.start"), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}
