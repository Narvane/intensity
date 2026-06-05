package com.intensity.mobile.app.ui.experience

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.core.experience.wizard.model.CreationStep
import com.intensity.mobile.app.core.experience.wizard.model.WizardState
import com.intensity.mobile.app.core.experience.wizard.policy.IntensitySuggestionPolicy
import com.intensity.mobile.app.core.experience.wizard.usecase.BuildExperienceRequestUseCase
import com.intensity.mobile.app.core.experience.wizard.usecase.MoveWizardStepUseCase
import com.intensity.mobile.app.core.experience.wizard.usecase.SubmitExperienceUseCase
import com.intensity.mobile.app.core.experience.wizard.validation.WizardValidationRules
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.adapters.experience.ExperienceCommandAdapter
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.IntensityPrimaryBrownButton
import com.intensity.mobile.app.ui.common.WizardProgressRow
import com.intensity.mobile.app.ui.common.WizardStepPill
import com.intensity.mobile.app.ui.theme.IntensityBrand
import com.intensity.mobile.shared.readableIntensityHttpError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceCreationWizard(
    experienceBoxType: String?,
    gateway: IntensityGateway,
    token: String,
    snackbars: androidx.compose.material3.SnackbarHostState,
    scope: CoroutineScope,
    onExperienceCreated: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moveWizardStepUseCase = remember { MoveWizardStepUseCase() }
    val buildExperienceRequestUseCase = remember { BuildExperienceRequestUseCase() }
    val validationRules = remember { WizardValidationRules() }
    val suggestionPolicy = remember { IntensitySuggestionPolicy() }
    val submitExperienceUseCase = remember(gateway) { SubmitExperienceUseCase(ExperienceCommandAdapter(gateway)) }

    var step by remember { mutableStateOf(CreationStep.Suggestion) }
    var description by remember { mutableStateOf("") }
    var reflectionJustification by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    val overlayInteraction = remember { MutableInteractionSource() }
    var effortStars by remember { mutableStateOf(0) }
    var opennessStars by remember { mutableStateOf(0) }
    var noveltyStars by remember { mutableStateOf(0) }
    var selectedIntensity by remember { mutableStateOf(3) }
    var intensityManualOverride by remember { mutableStateOf(false) }

    val wizardBodyScroll = rememberScrollState()
    val fieldFill = MaterialTheme.colorScheme.surfaceVariant
    val fieldBorder = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    val savedSuggestionMessage = t("wizard.message.saved_suggestion")
    val createdMessage = t("wizard.message.created")
    val describeExperienceMessage = t("wizard.validation.describe_experience")
    val justifyBeforeContinueMessage = t("wizard.validation.justify_before_continue")
    val rateAllCriteriaMessage = t("wizard.validation.rate_all_criteria")

    fun resetWizard() {
        step = CreationStep.Suggestion
        description = ""
        reflectionJustification = ""
        effortStars = 0
        opennessStars = 0
        noveltyStars = 0
        selectedIntensity = 3
        intensityManualOverride = false
    }

    fun currentState(): WizardState = WizardState(
        step = step,
        description = description,
        reflectionJustification = reflectionJustification,
        effortStars = effortStars,
        opennessStars = opennessStars,
        noveltyStars = noveltyStars,
        selectedIntensity = selectedIntensity,
        intensityManualOverride = intensityManualOverride
    )

    fun submitAndReset(doneMessage: String, dismissAfter: Boolean) {
        if (submitting) return
        submitting = true
        scope.launch {
            try {
                runCatching {
                    submitExperienceUseCase.execute(token, buildExperienceRequestUseCase.execute(currentState()))
                }.onSuccess {
                    resetWizard()
                    onExperienceCreated()
                    snackbars.showSnackbar(doneMessage)
                    if (dismissAfter) onDismissRequest()
                }.onFailure { err ->
                    scope.launch { snackbars.showSnackbar(readableIntensityHttpError(err)) }
                }
            } finally {
                submitting = false
            }
        }
    }

    LaunchedEffect(step, effortStars, opennessStars, noveltyStars) {
        if (step == CreationStep.Classification &&
            effortStars in 1..5 &&
            opennessStars in 1..5 &&
            noveltyStars in 1..5 &&
            !intensityManualOverride
        ) {
            selectedIntensity = suggestionPolicy.suggest(effortStars, opennessStars, noveltyStars)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            WizardDescriptionCard(
                description = description,
                onDescriptionChange = { description = it },
                fieldFill = fieldFill,
                fieldBorder = fieldBorder
            )

            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth()
                    .verticalScroll(wizardBodyScroll)
            ) {
                IntensityCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(t("wizard.title.register_experience"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        WizardProgressRow(currentStepIndex = stepIndex(step))
                        WizardStepPill(t(stepLabel(step)))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        when (step) {
                            CreationStep.Suggestion -> SuggestionStepContent(
                                experienceBoxType = experienceBoxType,
                                onPickSuggestion = { description = it }
                            )
                            CreationStep.Validation -> ReflectionStepContent(
                                value = reflectionJustification,
                                onValueChange = { reflectionJustification = it }
                            )
                            CreationStep.Parametrization -> ParametrizationStepContent(
                                effortStars = effortStars,
                                onEffort = { effortStars = it },
                                opennessStars = opennessStars,
                                onOpenness = { opennessStars = it },
                                noveltyStars = noveltyStars,
                                onNovelty = { noveltyStars = it }
                            )
                            CreationStep.Classification -> ClassificationStepContent(
                                suggestedText = if (effortStars in 1..5 && opennessStars in 1..5 && noveltyStars in 1..5) {
                                    "${t("wizard.classification.suggested_prefix")} ${suggestionPolicy.suggest(effortStars, opennessStars, noveltyStars)} ${t("wizard.classification.suggested_suffix")}"
                                } else {
                                    t("wizard.classification.select_final_intensity")
                                },
                                selectedIntensity = selectedIntensity,
                                onSelectIntensity = {
                                    intensityManualOverride = true
                                    selectedIntensity = it
                                }
                            )
                            CreationStep.Bifurcation -> BifurcationStepContent(
                                submitting = submitting,
                                onSaveAnother = { submitAndReset(savedSuggestionMessage, dismissAfter = false) }
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (step != CreationStep.Suggestion) {
                                TextButton(
                                    enabled = !submitting,
                                    onClick = {
                                        if (step == CreationStep.Classification) {
                                            intensityManualOverride = false
                                        }
                                        step = moveWizardStepUseCase.previous(step)
                                    }
                                ) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                                    Text(t("common.back"), modifier = Modifier.padding(start = 4.dp))
                                }
                            } else {
                                Spacer(Modifier.width(4.dp))
                            }

                            when (step) {
                                CreationStep.Bifurcation -> {
                                    Spacer(Modifier.weight(1f))
                                    Button(
                                        onClick = { if (!submitting) submitAndReset(createdMessage, dismissAfter = true) },
                                        enabled = !submitting,
                                        modifier = Modifier.size(56.dp),
                                        shape = CircleShape,
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = IntensityBrand.RoleParticipant,
                                            contentColor = Color.White,
                                            disabledContainerColor = IntensityBrand.RoleParticipant.copy(alpha = 0.5f),
                                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                    ) {
                                        Icon(Icons.Filled.Save, contentDescription = t("wizard.action.finish_and_back"))
                                    }
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        IntensityPrimaryBrownButton(
                                            text = t("common.continue"),
                                            enabled = !submitting,
                                            onClick = cont@{
                                                when (validationRules.validateNextStep(currentState())) {
                                                    "missing_description" -> {
                                                        scope.launch { snackbars.showSnackbar(describeExperienceMessage) }
                                                        return@cont
                                                    }
                                                    "missing_reflection" -> {
                                                        scope.launch { snackbars.showSnackbar(justifyBeforeContinueMessage) }
                                                        return@cont
                                                    }
                                                    "missing_stars" -> {
                                                        scope.launch { snackbars.showSnackbar(rateAllCriteriaMessage) }
                                                        return@cont
                                                    }
                                                    else -> {
                                                        if (step == CreationStep.Parametrization) {
                                                            intensityManualOverride = false
                                                        }
                                                        step = moveWizardStepUseCase.next(step)
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        if (submitting) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.28f))
                    .clickable(interactionSource = overlayInteraction, indication = null) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
