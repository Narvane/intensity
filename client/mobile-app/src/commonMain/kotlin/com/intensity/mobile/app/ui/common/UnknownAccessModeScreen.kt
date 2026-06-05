package com.intensity.mobile.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import com.intensity.mobile.app.platform.i18n.t
import com.intensity.mobile.app.ui.theme.IntensityBrand

private val SessionErrorTitle = Color(0xFF9B2C2C)

@Composable
fun UnknownAccessModeScreen(
    accessMode: String,
    onLogout: () -> Unit,
    onEnterAsParticipant: () -> Unit = onLogout
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(intensityScreenBackdropBrush()),
        contentAlignment = Alignment.Center
    ) {
        IntensityCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = SessionErrorTitle
                    )
                    Text(
                        text = t("unknownSession.title"),
                        style = MaterialTheme.typography.titleMedium,
                        color = SessionErrorTitle,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = t("unknownSession.modeLabel"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = accessMode,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = Color(0xFFF1F3F5),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${t("unknownSession.accessMode")} = $accessMode",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                IntensityPrimaryBlueButton(
                    text = t("buttons.exit"),
                    leadingIcon = Icons.AutoMirrored.Filled.ExitToApp,
                    onClick = onLogout
                )
                TextButton(
                    onClick = onEnterAsParticipant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        t("unknownSession.enterBox"),
                        color = IntensityBrand.RoleParticipant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
