package com.intensity.mobile.app.ui.experience

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.common.IntensityCapsLabel
import com.intensity.mobile.app.ui.common.IntensityCard
import com.intensity.mobile.app.ui.theme.IntensityBrand

@Composable
fun WizardDescriptionCard(
    description: String,
    onDescriptionChange: (String) -> Unit,
    fieldFill: Color,
    fieldBorder: Color
) {
    IntensityCard(elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Article, contentDescription = null, tint = IntensityBrand.RoleParticipant, modifier = Modifier.size(22.dp))
                IntensityCapsLabel(t("wizard.description.title"), modifier = Modifier.padding(bottom = 0.dp))
            }
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8,
                placeholder = {
                    Text(
                        t("wizard.description.placeholder"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = fieldFill,
                    unfocusedContainerColor = fieldFill,
                    disabledContainerColor = fieldFill,
                    focusedBorderColor = fieldBorder,
                    unfocusedBorderColor = fieldBorder,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Save, contentDescription = null, tint = IntensityBrand.RoleParticipant, modifier = Modifier.size(18.dp))
                Text(
                    t("wizard.description.not_encrypted"),
                    style = MaterialTheme.typography.labelSmall,
                    color = IntensityBrand.RoleParticipant
                )
            }
        }
    }
}
