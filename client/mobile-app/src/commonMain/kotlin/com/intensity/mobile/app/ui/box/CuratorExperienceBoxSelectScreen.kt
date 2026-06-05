package com.intensity.mobile.app.ui.box

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.shell.session.AppSession
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityGradientTopBar
import com.intensity.mobile.app.ui.common.IntensitySectionTitleRow
import com.intensity.mobile.app.ui.common.IntensitySmallCapsSectionLabel
import com.intensity.mobile.app.ui.common.IntensityTopBarBackIcon
import com.intensity.mobile.app.ui.common.IntensityTopBarBrandCenter
import com.intensity.mobile.app.ui.common.IntensityTopBarLogoutRow
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import com.intensity.mobile.app.ui.session.ExperienceBoxTypeCatalog
import com.intensity.contracts.model.BoxDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuratorExperienceBoxSelectScreen(
    gateway: IntensityGateway,
    session: AppSession,
    onSessionUpdated: (AppSession) -> Unit,
    onBackToSpaces: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    var loading by remember { mutableStateOf(true) }
    var boxes by remember { mutableStateOf<List<BoxDto>>(emptyList()) }
    val errorLoadingText = t("box.select.error_loading")
    val serverInvalidText = t("groups.serverInvalid")
    val commonFailureText = t("common.failure")

    LaunchedEffect(session.token, session.groupId) {
        loading = true
        runCatching { gateway.listBoxes(session.token) }
            .onSuccess { boxes = it }
            .onFailure { snackbars.showSnackbar("$errorLoadingText: ${it.message}") }
        loading = false
    }

    Scaffold(
        topBar = {
            IntensityGradientTopBar(
                centerContent = { IntensityTopBarBrandCenter() },
                navigation = { IntensityTopBarBackIcon(onClick = onBackToSpaces) },
                actions = { IntensityTopBarLogoutRow(onLogout) }
            )
        },
        snackbarHost = { SnackbarHost(snackbars) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(intensityScreenBackdropBrush())
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IntensitySectionTitleRow(
                    text = t("box.select.title"),
                    icon = Icons.Filled.Apps,
                    iconTint = MaterialTheme.colorScheme.primary
                )
                Text(
                    t("box.select.subtitle"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IntensitySmallCapsSectionLabel(
                    text = t("session.boxes.title_short"),
                    icon = Icons.Filled.Apps,
                    color = MaterialTheme.colorScheme.primary
                )
                if (loading) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (boxes.isEmpty()) {
                    Text(
                        t("box.select.empty"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(boxes, key = { _, b -> b.id }) { _, box ->
                            val type = ExperienceBoxTypeCatalog.optionFor(box.boxType)
                            BoxVisualCard(
                                name = box.name,
                                subtitle = null,
                                badgeIcon = type?.icon,
                                badgeText = type?.titleKey?.let { t(it) } ?: t(ExperienceBoxTypeCatalog.shortLabel(box.boxType)),
                                paletteAccentColor = type?.palette?.accent,
                                paletteSurfaceColor = type?.palette?.surface,
                                accentBlue = false,
                                onClick = {
                                    scope.launch {
                                        runCatching {
                                            gateway.selectCurateExperienceBox(session.token, box.id)
                                        }.onSuccess { resp ->
                                            val next = AppSession.from(resp)
                                            if (next != null) onSessionUpdated(next)
                                            else snackbars.showSnackbar(serverInvalidText)
                                        }.onFailure {
                                            snackbars.showSnackbar("$commonFailureText: ${it.message}")
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
}
