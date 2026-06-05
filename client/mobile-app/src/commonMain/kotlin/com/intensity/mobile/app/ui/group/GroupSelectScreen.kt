package com.intensity.mobile.app.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.shell.session.AppSession
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.IntensityGradientTopBar
import com.intensity.mobile.app.ui.common.IntensitySectionTitleRow
import com.intensity.mobile.app.ui.common.IntensityTopBarBrandCenter
import com.intensity.mobile.app.ui.common.IntensityTopBarLogoutRow
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import com.intensity.contracts.model.GroupDetailDto
import kotlinx.coroutines.launch

private fun groupLabel(group: GroupDetailDto): String {
    return group.participants.joinToString(" + ") { it.name }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelectScreen(
    gateway: IntensityGateway,
    session: AppSession,
    onSessionUpdated: (AppSession) -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    var loading by remember { mutableStateOf(true) }
    var groups by remember { mutableStateOf<List<GroupDetailDto>>(emptyList()) }
    val invalidServerResponse = t("groups.serverInvalid")
    val errorPrefix = t("common.errorPrefix")
    val errorLoadingText = t("groups.error_loading")

    LaunchedEffect(session.token) {
        loading = true
        runCatching { gateway.listGroups(session.token) }
            .onSuccess { groups = it }
            .onFailure { snackbars.showSnackbar("$errorLoadingText: ${it.message}") }
        loading = false
    }

    Scaffold(
        topBar = {
            IntensityGradientTopBar(
                centerContent = { IntensityTopBarBrandCenter() },
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
                    text = t("groups.title"),
                    icon = Icons.Filled.Apps,
                    iconTint = MaterialTheme.colorScheme.primary
                )
                Text(
                    t("groups.subtitle"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (loading) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (groups.isEmpty()) {
                    IntensityCard {
                        Text(
                            t("groups.empty"),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(groups, key = { it.id }) { group ->
                            IntensityCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            runCatching {
                                                gateway.selectCurateGroup(session.token, group.id)
                                            }.onSuccess { resp ->
                                                val next = AppSession.from(resp)
                                                if (next != null) onSessionUpdated(next)
                                                else snackbars.showSnackbar(invalidServerResponse)
                                            }.onFailure {
                                                snackbars.showSnackbar("$errorPrefix: ${it.message}")
                                            }
                                        }
                                    }
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(groupLabel(group), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(
                                                Icons.Filled.Groups,
                                                null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                            Text(
                                                t("groups.participantsCount", group.participants.size),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            t("buttons.enter"),
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Icon(
                                            Icons.Filled.ChevronRight,
                                            null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
