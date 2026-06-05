package com.intensity.mobile.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.IntensityPrimaryBrownButton

@Composable
fun RegisterPane(
    name: String,
    email: String,
    password: String,
    loading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
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
                    Icons.Filled.Dashboard,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    t("auth.register.title"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                t("auth.register.subtitle"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(t("auth.fields.name")) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary) },
                placeholder = { Text(t("auth.fields.name_placeholder")) },
                colors = authFieldColors(),
                shape = MaterialTheme.shapes.medium
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
                placeholder = { Text(t("auth.fields.password_min")) },
                colors = authFieldColors(),
                shape = MaterialTheme.shapes.medium
            )
            IntensityPrimaryBrownButton(
                text = if (loading) t("auth.actions.registering") else t("auth.actions.register"),
                enabled = !loading,
                leadingIcon = Icons.Filled.PersonAdd,
                onClick = onSubmit
            )
        }
    }
}
