package com.intensity.mobile.app.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
internal fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.primary,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
)

internal fun readableError(error: Throwable, unknownFallback: String): String {
    return sequenceOf(error.message, error.cause?.message)
        .firstOrNull { !it.isNullOrBlank() }
        ?: unknownFallback
}
