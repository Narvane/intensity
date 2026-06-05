package com.intensity.mobile.app.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.intensity.mobile.app.ui.theme.IntensityBrand

@Composable
fun IntensityPrimaryBrownButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        if (leadingIcon != null) {
            androidx.compose.material3.Icon(leadingIcon, null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(10.dp))
        }
        Text(text, fontWeight = FontWeight.Black)
    }
}

@Composable
fun IntensityPrimaryBlueButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = IntensityBrand.RoleParticipant,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        if (leadingIcon != null) {
            androidx.compose.material3.Icon(leadingIcon, null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(10.dp))
        }
        Text(text, fontWeight = FontWeight.Black)
    }
}
