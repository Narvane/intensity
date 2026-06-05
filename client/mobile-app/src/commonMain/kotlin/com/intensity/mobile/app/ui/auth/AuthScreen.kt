package com.intensity.mobile.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.contracts.model.ConnectMemberCredentialRequestDto
import com.intensity.mobile.app.core.auth.model.AuthMode
import com.intensity.mobile.app.core.auth.model.ConnectCredential
import com.intensity.mobile.app.core.auth.usecase.LoginConnectUseCase
import com.intensity.mobile.app.core.auth.usecase.LoginCurateUseCase
import com.intensity.mobile.app.core.auth.usecase.RegisterUseCase
import com.intensity.mobile.app.core.auth.usecase.ValidateConnectCredentialsUseCase
import com.intensity.mobile.app.shell.session.AppSession
import com.intensity.mobile.app.adapters.resourceapi.IntensityGateway
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.adapters.auth.AuthGatewayAdapter
import com.intensity.mobile.app.ui.common.AuthSegmentVariant
import com.intensity.mobile.app.ui.common.IntensityAuthBrandHeader
import com.intensity.mobile.app.ui.common.IntensityAuthModeCard
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.LanguageSelector
import com.intensity.mobile.app.ui.common.intensityScreenBackdropBrush
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    gateway: IntensityGateway,
    onAuthenticated: (AppSession) -> Unit,
    onOpenManual: () -> Unit,
    onOpenOnboarding: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbars = remember { SnackbarHostState() }
    val authGateway = remember(gateway) { AuthGatewayAdapter(gateway) }
    val loginCurateUseCase = remember(authGateway) { LoginCurateUseCase(authGateway) }
    val loginConnectUseCase = remember(authGateway) { LoginConnectUseCase(authGateway) }
    val registerUseCase = remember(authGateway) { RegisterUseCase(authGateway) }
    val validateConnectCredentialsUseCase = remember { ValidateConnectCredentialsUseCase() }

    var mode by remember { mutableStateOf(AuthMode.CURATE_LOGIN) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val experienceRows = remember { mutableStateListOf(ConnectCredential(email = "", password = "")) }

    val tokenNotReturnedText = t("auth.error.token_not_returned")
    val tokenSessionNotReturnedText = t("auth.error.session_token_not_returned")
    val loginFailText = t("auth.error.login_failed")
    val fillAllCredentialsText = t("auth.error.fill_all_credentials")
    val registerDoneText = t("auth.register.done")
    val registerFailText = t("auth.error.register_failed")
    val unknownErrorText = t("common.error_unknown")

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbars) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(intensityScreenBackdropBrush())
                .padding(padding)
        ) {
            IntensityAuthBrandHeader()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LanguageSelector(modifier = Modifier.padding(start = 12.dp))
                Row {
                    IconButton(onClick = onOpenOnboarding) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = t("onboarding.reopen.content"),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = onOpenManual) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = t("auth.manual.content"),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    IntensityCard {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    Icons.Filled.WavingHand,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                                Text(
                                    t("auth.welcome"),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                t("auth.intro"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                IntensityAuthModeCard(
                                    title = t("auth.mode.curate.title"),
                                    subtitle = t("auth.mode.curate.subtitle"),
                                    icon = Icons.Filled.Person,
                                    selected = mode == AuthMode.CURATE_LOGIN,
                                    variant = AuthSegmentVariant.Brown,
                                    onClick = { mode = AuthMode.CURATE_LOGIN },
                                    modifier = Modifier.weight(1f)
                                )
                                IntensityAuthModeCard(
                                    title = t("auth.mode.connect.title"),
                                    subtitle = t("auth.mode.connect.subtitle"),
                                    icon = Icons.Filled.Groups,
                                    selected = mode == AuthMode.CONNECT_LOGIN,
                                    variant = AuthSegmentVariant.Blue,
                                    onClick = { mode = AuthMode.CONNECT_LOGIN },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            IntensityAuthModeCard(
                                title = t("auth.mode.register.title"),
                                subtitle = t("auth.mode.register.subtitle"),
                                icon = Icons.Filled.PersonAdd,
                                selected = mode == AuthMode.REGISTER,
                                variant = AuthSegmentVariant.Blue,
                                onClick = { mode = AuthMode.REGISTER },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                when (mode) {
                    AuthMode.CURATE_LOGIN -> item {
                        CurateLoginPane(
                            email = email,
                            password = password,
                            loading = loading,
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            onSubmit = {
                                loading = true
                                scope.launch {
                                    runCatching { loginCurateUseCase.execute(email, password) }
                                        .onSuccess { response ->
                                            val sess = AppSession.from(response)
                                            if (sess != null) onAuthenticated(sess) else snackbars.showSnackbar(tokenNotReturnedText)
                                        }
                                        .onFailure { snackbars.showSnackbar("$loginFailText: ${it.message}") }
                                    loading = false
                                }
                            }
                        )
                    }

                    AuthMode.CONNECT_LOGIN -> connectLoginPane(
                        rows = experienceRows,
                        loading = loading,
                        onAddRow = { experienceRows.add(ConnectCredential(email = "", password = "")) },
                        onRemoveRow = { experienceRows.remove(it) },
                        onUpdateRow = { row, nextEmail, nextPassword ->
                            val i = experienceRows.indexOf(row)
                            if (i >= 0) {
                                experienceRows[i] = experienceRows[i].copy(email = nextEmail, password = nextPassword)
                            }
                        },
                        onSubmit = {
                            val creds: List<ConnectMemberCredentialRequestDto> = runCatching {
                                validateConnectCredentialsUseCase.execute(experienceRows)
                            }.getOrElse {
                                scope.launch { snackbars.showSnackbar(fillAllCredentialsText) }
                                return@connectLoginPane
                            }
                            loading = true
                            scope.launch {
                                runCatching { loginConnectUseCase.execute(creds) }
                                    .onSuccess { response ->
                                        val sess = AppSession.from(response)
                                        if (sess != null) onAuthenticated(sess) else snackbars.showSnackbar(tokenSessionNotReturnedText)
                                    }
                                    .onFailure { snackbars.showSnackbar("$loginFailText: ${it.message}") }
                                loading = false
                            }
                        }
                    )

                    AuthMode.REGISTER -> item {
                        RegisterPane(
                            name = name,
                            email = email,
                            password = password,
                            loading = loading,
                            onNameChange = { name = it },
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            onSubmit = {
                                loading = true
                                scope.launch {
                                    runCatching { registerUseCase.execute(name, email, password) }
                                        .onSuccess {
                                            snackbars.showSnackbar(registerDoneText)
                                            name = ""
                                            email = ""
                                            password = ""
                                            mode = AuthMode.CURATE_LOGIN
                                        }
                                        .onFailure {
                                            snackbars.showSnackbar("$registerFailText: ${readableError(it, unknownErrorText)}")
                                        }
                                    loading = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
