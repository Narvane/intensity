package com.intensity.mobile.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.intensity.mobile.app.shell.AppBootstrapCoordinator
import com.intensity.mobile.app.shell.BootstrapState
import com.intensity.mobile.app.shell.SessionRoute
import com.intensity.mobile.app.shell.SessionRouteResolver
import com.intensity.mobile.app.shell.session.AppSession
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.adapters.persistence.IntensityLanguageStore
import com.intensity.mobile.app.adapters.persistence.IntensityOnboardingStore
import com.intensity.mobile.app.platform.i18n.LanguageState
import com.intensity.mobile.app.platform.i18n.LocalLanguageState
import com.intensity.mobile.app.ui.auth.AuthScreen
import com.intensity.mobile.app.ui.onboarding.IntensityOnboardingScreen
import com.intensity.mobile.app.ui.onboarding.IntensityManualScreen
import com.intensity.mobile.app.ui.box.CuratorExperienceBoxSelectScreen
import com.intensity.mobile.app.ui.experience.ExperienceWizardScreen
import com.intensity.mobile.app.ui.experience.ExperiencesScreen
import com.intensity.mobile.app.ui.common.UnknownAccessModeScreen
import com.intensity.mobile.app.ui.session.ExperienceBoxHomeScreen
import com.intensity.mobile.app.ui.session.SharedMomentScreen
import com.intensity.mobile.app.ui.group.GroupSelectScreen
import com.intensity.mobile.app.ui.theme.IntensityTheme

@Composable
fun IntensityApp() {
    val gateway = remember { IntensityGateway() }
    val bootstrapCoordinator = remember { AppBootstrapCoordinator() }
    val sessionRouteResolver = remember { SessionRouteResolver() }
    var session by remember { mutableStateOf<AppSession?>(null) }
    var bootstrapState by remember { mutableStateOf(BootstrapState()) }
    var showHelpManual by remember { mutableStateOf(false) }
    var showOnboardingAgain by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bootstrapState = bootstrapCoordinator.loadBootstrapState()
    }

    IntensityTheme {
        CompositionLocalProvider(
            LocalLanguageState provides LanguageState(
                language = bootstrapState.selectedLanguage,
                onLanguageChange = {
                    bootstrapState = bootstrapState.copy(selectedLanguage = it)
                    IntensityLanguageStore.setLanguage(it)
                }
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                when (val s = session) {
                    null -> {
                        when {
                            !bootstrapState.onboardingPrefsReady || !bootstrapState.languageReady -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            bootstrapState.needsFirstRunOnboarding -> {
                                if (showHelpManual) {
                                    IntensityManualScreen(
                                        isFirstRun = true,
                                        onDismiss = {
                                            IntensityOnboardingStore.markIntroManualSeen()
                                            bootstrapState = bootstrapState.copy(needsFirstRunOnboarding = false)
                                            showHelpManual = false
                                        }
                                    )
                                } else {
                                    IntensityOnboardingScreen(
                                        isFirstRun = true,
                                        onFinish = {
                                            IntensityOnboardingStore.markIntroManualSeen()
                                            bootstrapState = bootstrapState.copy(needsFirstRunOnboarding = false)
                                        },
                                        onOpenManual = { showHelpManual = true }
                                    )
                                }
                            }
                            else -> {
                                Box(Modifier.fillMaxSize()) {
                                    AuthScreen(
                                        gateway = gateway,
                                        onAuthenticated = { authenticated -> session = authenticated },
                                        onOpenManual = { showHelpManual = true },
                                        onOpenOnboarding = { showOnboardingAgain = true }
                                    )
                                    if (showOnboardingAgain) {
                                        IntensityOnboardingScreen(
                                            isFirstRun = false,
                                            onFinish = { showOnboardingAgain = false },
                                            onOpenManual = {
                                                showOnboardingAgain = false
                                                showHelpManual = true
                                            }
                                        )
                                    } else if (showHelpManual) {
                                        IntensityManualScreen(
                                            isFirstRun = false,
                                            onDismiss = { showHelpManual = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> when (sessionRouteResolver.resolve(s)) {
                    SessionRoute.CONNECT -> {
                        var selectedBoxId by remember(s.token) { mutableStateOf<String?>(null) }
                        if (selectedBoxId == null) {
                            ExperienceBoxHomeScreen(
                                gateway = gateway,
                                session = s,
                                onOpenBox = { selectedBoxId = it },
                                onLogout = { session = null }
                            )
                        } else {
                            SharedMomentScreen(
                                gateway = gateway,
                                session = s,
                                boxId = selectedBoxId!!,
                                onBack = { selectedBoxId = null },
                                onLogout = { session = null }
                            )
                        }
                    }
                    SessionRoute.CURATE -> when {
                        s.groupId == null -> GroupSelectScreen(
                            gateway = gateway,
                            session = s,
                            onSessionUpdated = { session = it },
                            onLogout = { session = null }
                        )
                        s.boxId == null -> CuratorExperienceBoxSelectScreen(
                            gateway = gateway,
                            session = s,
                            onSessionUpdated = { session = it },
                            onBackToSpaces = { session = s.copy(groupId = null, boxId = null, experienceBoxType = null) },
                            onLogout = { session = null }
                        )
                        else -> {
                            var wizardOpen by remember(s.token, s.groupId, s.boxId) { mutableStateOf(false) }
                            var experiencesRefresh by remember(s.token, s.groupId, s.boxId) { mutableStateOf(0) }
                            Box(Modifier.fillMaxSize()) {
                                ExperiencesScreen(
                                    gateway = gateway,
                                    session = s,
                                    onLogout = { session = null },
                                    onCreateExperience = { wizardOpen = true },
                                    onNavigateBack = { session = s.copy(boxId = null, experienceBoxType = null) },
                                    refreshSignal = experiencesRefresh
                                )
                                if (wizardOpen) {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.background
                                    ) {
                                        ExperienceWizardScreen(
                                            gateway = gateway,
                                            session = s,
                                            onClose = { wizardOpen = false },
                                            onExperienceCreated = { experiencesRefresh++ }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    SessionRoute.UNKNOWN -> UnknownAccessModeScreen(
                        accessMode = s.accessMode,
                        onLogout = { session = null },
                        onEnterAsParticipant = { session = null }
                    )
                    }
                }
            }
        }
    }
}
