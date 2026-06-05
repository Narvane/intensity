package com.intensity.mobile.app.ui.experience

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
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
import com.intensity.mobile.app.ui.common.IntensityPrimaryBlueButton
import com.intensity.mobile.app.ui.common.IntensitySmallCapsSectionLabel
import com.intensity.mobile.app.ui.common.IntensityTopBarBackIcon
import com.intensity.mobile.app.ui.common.IntensityTopBarLogoutRow
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import com.intensity.mobile.app.ui.session.ExperienceBoxTypeCatalog
import com.intensity.mobile.app.ui.session.ExperienceBoxTypeCode
import com.intensity.contracts.model.BoxDto
import com.intensity.contracts.model.ExperienceSummaryDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperiencesScreen(
    gateway: IntensityGateway,
    session: AppSession,
    onLogout: () -> Unit,
    onCreateExperience: () -> Unit,
    onNavigateBack: () -> Unit,
    refreshSignal: Int = 0
) {
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    var summaries by remember { mutableStateOf<List<ExperienceSummaryDto>>(emptyList()) }
    var loadingList by remember { mutableStateOf(false) }
    val defaultBoxTitle = t("box.default_name")
    val listErrorText = t("experience.list.error")
    var boxTitle by remember(defaultBoxTitle) { mutableStateOf(defaultBoxTitle) }
    var boxType by remember { mutableStateOf(ExperienceBoxTypeCode.DEFAULT.code) }

    suspend fun refreshList() {
        loadingList = true
        runCatching { gateway.listExperienceSummaries(session.token) }
            .onSuccess { summaries = it }
            .onFailure { snackbars.showSnackbar("$listErrorText: ${it.message}") }
        loadingList = false
    }

    LaunchedEffect(session.token, session.boxId, refreshSignal) {
        refreshList()
        runCatching { gateway.listBoxes(session.token) }
            .onSuccess { boxes: List<BoxDto> ->
                val id = session.boxId
                if (id != null) {
                    val selected = boxes.find { it.id == id }
                    boxTitle = selected?.name ?: defaultBoxTitle
                    boxType = selected?.boxType ?: ExperienceBoxTypeCode.DEFAULT.code
                }
            }
    }

    val myId = session.curateUserId
    val visibleSummaries = remember(summaries, myId) {
        if (myId == null) emptyList() else summaries.filter { it.createdBy == myId }
    }

    Scaffold(
        topBar = {
            IntensityGradientTopBar(
                title = boxTitle,
                backgroundBrush = ExperienceBoxTypeCatalog.topBarBrushFor(boxType),
                navigation = { IntensityTopBarBackIcon(onClick = onNavigateBack) },
                actions = { IntensityTopBarLogoutRow(onLogout) }
            )
        },
        snackbarHost = { SnackbarHost(snackbars) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(intensityScreenBackdropBrush())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        Icons.Filled.MailOutline,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            t("experience.list.title"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            t("experience.list.subtitle"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                IntensityPrimaryBlueButton(
                    text = t("experience.actions.create"),
                    onClick = onCreateExperience
                )
            }
            item {
                IntensitySmallCapsSectionLabel(
                    text = t("experience.list.registered"),
                    icon = Icons.Filled.MailOutline,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (loadingList) CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                }
            }
            if (!loadingList && visibleSummaries.isEmpty()) {
                item {
                    IntensityCard {
                        Text(
                            t("experience.list.empty"),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(visibleSummaries, key = { it.id }) { s ->
                    ExperienceSummaryCard(
                        summary = s,
                        myUserId = session.curateUserId,
                        gateway = gateway,
                        session = session,
                        onChanged = { scope.launch { refreshList() } },
                        snackbars = snackbars
                    )
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
