package com.intensity.mobile.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.IntensityPrimaryBrownButton

@Composable
fun CurateLoginPane(
    email: String,
    password: String,
    loading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    IntensityCard {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Login,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    t("auth.curate.title"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                t("auth.curate.subtitle"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text(t("auth.fields.email")) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Email, null, tint = MaterialTheme.colorScheme.primary) },
                placeholder = { Text(t("auth.fields.email_placeholder")) },
                colors = authFieldColors(),
                shape = MaterialTheme.shapes.medium
            )
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(t("auth.fields.password")) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                colors = authFieldColors(),
                shape = MaterialTheme.shapes.medium
            )
            IntensityPrimaryBrownButton(
                text = if (loading) t("auth.actions.entering") else t("buttons.enter"),
                enabled = !loading,
                leadingIcon = Icons.AutoMirrored.Filled.Login,
                onClick = onSubmit
            )
        }
    }
}
