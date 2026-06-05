package com.intensity.mobile.app.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.contracts.model.BoxDto
import com.intensity.mobile.app.shell.session.AppSession
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.box.BoxVisualCard
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.IntensityGradientTopBar
import com.intensity.mobile.app.ui.common.IntensityPrimaryBrownButton
import com.intensity.mobile.app.ui.common.IntensitySectionTitleRow
import com.intensity.mobile.app.ui.common.IntensitySmallCapsSectionLabel
import com.intensity.mobile.app.ui.common.IntensityTopBarBrandCenter
import com.intensity.mobile.app.ui.common.IntensityTopBarLogoutRow
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import com.intensity.mobile.app.ui.theme.IntensityBrand
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceBoxHomeScreen(
    gateway: IntensityGateway,
    session: AppSession,
    onOpenBox: (String) -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    var loading by remember { mutableStateOf(true) }
    var boxes by remember { mutableStateOf<List<BoxDto>>(emptyList()) }
    var showCreateBoxScreen by remember { mutableStateOf(false) }
    val errorPrefixText = t("common.errorPrefix")
    val boxCreatedText = t("session.boxes.created")

    suspend fun refresh() {
        loading = true
        runCatching { gateway.listBoxes(session.token) }
            .onSuccess { boxes = it }
            .onFailure { snackbars.showSnackbar("$errorPrefixText: ${it.message}") }
        loading = false
    }

    LaunchedEffect(session.token) { refresh() }

    if (showCreateBoxScreen) {
        ExperienceBoxCreateScreen(
            gateway = gateway,
            session = session,
            onBack = { showCreateBoxScreen = false },
            onLogout = onLogout,
            onCreated = {
                showCreateBoxScreen = false
                scope.launch {
                    refresh()
                    snackbars.showSnackbar(boxCreatedText)
                }
            }
        )
        return
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(intensityScreenBackdropBrush())
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IntensitySectionTitleRow(
                text = t("session.boxes.yours"),
                icon = Icons.Filled.Apps,
                iconTint = MaterialTheme.colorScheme.primary
            )
            Text(
                t("session.boxes.subtitle"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IntensityPrimaryBrownButton(
                text = t("session.boxes.create"),
                enabled = true,
                leadingIcon = Icons.Filled.Add,
                onClick = { showCreateBoxScreen = true }
            )
            if (loading) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (boxes.isEmpty()) {
                Text(t("session.boxes.empty"), color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                IntensitySmallCapsSectionLabel(
                    text = t("session.boxes.open"),
                    icon = Icons.Filled.GridOn,
                    color = IntensityBrand.RoleParticipant
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 520.dp)
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
                            onClick = { onOpenBox(box.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExperienceBoxCreateScreen(
    gateway: IntensityGateway,
    session: AppSession,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onCreated: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    var newBoxName by remember { mutableStateOf("") }
    var selectedBoxType by remember { mutableStateOf(ExperienceBoxTypeCode.DEFAULT.code) }
    var creating by remember { mutableStateOf(false) }
    val errorPrefixText = t("common.errorPrefix")
    val typeNameHintText = t("session.boxes.enter_name")

    Scaffold(
        topBar = {
            IntensityGradientTopBar(
                title = t("session.boxes.create"),
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
                        Text(
                            t("session.boxes.title_short"),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 2.dp)
                        )
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
            IntensitySectionTitleRow(
                text = t("session.boxes.new"),
                icon = Icons.Filled.PostAdd,
                iconTint = MaterialTheme.colorScheme.primary
            )
            Text(
                t("session.boxes.new_hint"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IntensityCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        t("session.boxes.type"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExperienceBoxTypeCatalog.options.forEach { opt ->
                            ExperienceBoxTypeOptionCard(
                                option = opt,
                                selected = selectedBoxType == opt.code,
                                onClick = { selectedBoxType = opt.code }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = newBoxName,
                        onValueChange = { newBoxName = it },
                        label = { Text(t("session.boxes.name")) },
                        placeholder = { Text(t("session.boxes.name_example")) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                    IntensityPrimaryBrownButton(
                        text = if (creating) t("session.boxes.creating") else t("session.boxes.create"),
                        enabled = !creating,
                        leadingIcon = Icons.Filled.Add,
                        onClick = {
                            if (newBoxName.isBlank()) {
                                scope.launch { snackbars.showSnackbar(typeNameHintText) }
                            } else {
                                creating = true
                                scope.launch {
                                    runCatching { gateway.createBox(session.token, newBoxName.trim(), selectedBoxType) }
                                        .onSuccess { onCreated() }
                                        .onFailure { snackbars.showSnackbar("$errorPrefixText: ${it.message}") }
                                    creating = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
