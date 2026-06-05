package com.intensity.mobile.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.core.auth.model.ConnectCredential
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.common.IntensityPrimaryBlueButton
import com.intensity.mobile.app.ui.theme.IntensityBrand

fun LazyListScope.connectLoginPane(
    rows: List<ConnectCredential>,
    loading: Boolean,
    onAddRow: () -> Unit,
    onRemoveRow: (ConnectCredential) -> Unit,
    onUpdateRow: (ConnectCredential, String, String) -> Unit,
    onSubmit: () -> Unit
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
                        Icons.Filled.Groups,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        t("auth.connect.title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    t("auth.connect.subtitle"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = onAddRow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        t("auth.connect.add_user"),
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    items(rows, key = { it.key }) { row ->
        val index = rows.indexOf(row)
        IntensityCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(IntensityBrand.RoleParticipantSurface, Color.White)))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Inventory2,
                            contentDescription = null,
                            tint = IntensityBrand.RoleParticipant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "${t("auth.connect.user")} ${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            color = IntensityBrand.RoleParticipant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (rows.size > 1) {
                        IconButton(onClick = { onRemoveRow(row) }) {
                            Icon(Icons.Filled.RemoveCircle, t("auth.connect.remove_user"), tint = Color(0xFFC62828))
                        }
                    }
                }

                OutlinedTextField(
                    value = row.email,
                    onValueChange = { onUpdateRow(row, it, row.password) },
                    label = { Text(t("auth.fields.email")) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = IntensityBrand.RoleParticipant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = IntensityBrand.RoleParticipant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = row.password,
                    onValueChange = { onUpdateRow(row, row.email, it) },
                    label = { Text(t("auth.fields.password")) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Lock, null, tint = IntensityBrand.RoleParticipant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = IntensityBrand.RoleParticipant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = MaterialTheme.shapes.medium
                )
            }
        }
    }

    item {
        Spacer(Modifier.size(4.dp))
        IntensityPrimaryBlueButton(
            text = if (loading) t("auth.actions.entering") else t("buttons.enter"),
            enabled = !loading,
            leadingIcon = Icons.AutoMirrored.Filled.Login,
            onClick = onSubmit
        )
    }
}
