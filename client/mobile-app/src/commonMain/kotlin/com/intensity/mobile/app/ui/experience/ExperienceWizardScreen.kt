package com.intensity.mobile.app.ui.experience

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.shell.session.AppSession
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import com.intensity.mobile.app.ui.common.IntensityGradientTopBar
import com.intensity.mobile.app.ui.common.IntensityTopBarBrandCenter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceWizardScreen(
    gateway: IntensityGateway,
    session: AppSession,
    onClose: () -> Unit,
    onExperienceCreated: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbars) },
        topBar = {
            IntensityGradientTopBar(
                centerContent = { IntensityTopBarBrandCenter() },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, t("buttons.exit"), tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(intensityScreenBackdropBrush())
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            ExperienceCreationWizard(
                experienceBoxType = session.experienceBoxType,
                gateway = gateway,
                token = session.token,
                snackbars = snackbars,
                scope = scope,
                onExperienceCreated = onExperienceCreated,
                onDismissRequest = onClose,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
