package com.intensity.mobile.app.ui.experience

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.core.experience.wizard.model.CreationStep
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.EffortParamColor
import com.intensity.mobile.app.ui.common.EffortParamIcon
import com.intensity.mobile.app.ui.common.NoveltyParamColor
import com.intensity.mobile.app.ui.common.NoveltyParamIcon
import com.intensity.mobile.app.ui.common.OpennessParamColor
import com.intensity.mobile.app.ui.common.OpennessParamIcon
import com.intensity.mobile.app.ui.common.StarRatingRow
import com.intensity.mobile.app.ui.theme.IntensityBrand

private const val ExperienceReflectionQuestionKey = "wizard.reflection.question"

fun stepLabel(s: CreationStep): String = when (s) {
    CreationStep.Suggestion -> "wizard.step.suggestion"
    CreationStep.Validation -> "wizard.step.reflection"
    CreationStep.Parametrization -> "wizard.step.parametrization"
    CreationStep.Classification -> "wizard.step.classification"
    CreationStep.Bifurcation -> "wizard.step.bifurcation"
}

fun stepIndex(s: CreationStep): Int = when (s) {
    CreationStep.Suggestion -> 0
    CreationStep.Validation -> 1
    CreationStep.Parametrization -> 2
    CreationStep.Classification -> 3
    CreationStep.Bifurcation -> 4
}

@Composable
private fun WizardSectionTitle(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = IntensityBrand.RoleParticipant, modifier = Modifier.size(24.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionStepContent(
    value: String,
    onValueChange: (String) -> Unit
) {
    WizardSectionTitle(Icons.Filled.Assignment, t("wizard.reflection.title"))
    Text(
        t(ExperienceReflectionQuestionKey),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(t("wizard.reflection.justification")) },
        placeholder = { Text(t("wizard.reflection.placeholder"), style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White
        )
    )
}

@Composable
fun ParametrizationStepContent(
    effortStars: Int,
    onEffort: (Int) -> Unit,
    opennessStars: Int,
    onOpenness: (Int) -> Unit,
    noveltyStars: Int,
    onNovelty: (Int) -> Unit
) {
    WizardSectionTitle(Icons.Filled.Tune, t("wizard.parametrization.title"))
    Text(
        t("wizard.parametrization.hint"),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    ratingCard(IntensityBrand.ParamEffortSurface) {
        StarRatingRow(
            label = "wizard.criteria.effort",
            criterionHelp = "wizard.criteria.effort_help",
            value = effortStars,
            onValueChange = onEffort,
            levelDescription = ExperienceRatingSemantics::effortLevel,
            leadingIcon = EffortParamIcon,
            iconTint = EffortParamColor
        )
    }
    ratingCard(IntensityBrand.ParamDiscomfortSurface) {
        StarRatingRow(
            label = "wizard.criteria.openness",
            criterionHelp = ExperienceRatingSemantics.opennessHelp,
            value = opennessStars,
            onValueChange = onOpenness,
            levelDescription = ExperienceRatingSemantics::opennessLevel,
            leadingIcon = OpennessParamIcon,
            iconTint = OpennessParamColor
        )
    }
    ratingCard(IntensityBrand.ParamDaringSurface) {
        StarRatingRow(
            label = "wizard.criteria.novelty",
            criterionHelp = ExperienceRatingSemantics.noveltyHelp,
            value = noveltyStars,
            onValueChange = onNovelty,
            levelDescription = ExperienceRatingSemantics::noveltyLevel,
            leadingIcon = NoveltyParamIcon,
            iconTint = NoveltyParamColor
        )
    }
}

@Composable
private fun ratingCard(containerColor: Color, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    ) {
        Column(Modifier.padding(12.dp)) { content() }
    }
}

@Composable
fun ClassificationStepContent(
    suggestedText: String,
    selectedIntensity: Int,
    onSelectIntensity: (Int) -> Unit
) {
    WizardSectionTitle(Icons.Filled.Star, t("wizard.classification.title"))
    Text(
        suggestedText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(Modifier.height(4.dp))
    Text(
        t("wizard.classification.final_intensity_label"),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            IconButton(onClick = { onSelectIntensity(i) }) {
                Icon(
                    imageVector = if (selectedIntensity > 0 && i <= selectedIntensity) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "$i ${t("common.stars")}",
                    tint = if (selectedIntensity > 0 && i <= selectedIntensity) IntensityBrand.RatingStar else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun BifurcationStepContent(
    submitting: Boolean,
    onSaveAnother: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WizardSectionTitle(Icons.Filled.Category, t("wizard.bifurcation.title"))
            Text(
                t("wizard.bifurcation.hint"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onSaveAnother,
                enabled = !submitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    t("wizard.bifurcation.save_and_create_another"),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
