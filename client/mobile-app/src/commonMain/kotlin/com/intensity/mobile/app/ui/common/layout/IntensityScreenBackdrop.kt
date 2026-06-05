package com.intensity.mobile.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

@Composable
fun intensityScreenBackdropBrush(): Brush {
    val warm = MaterialTheme.colorScheme.surface
    return Brush.verticalGradient(
        colors = listOf(
            warm,
            Color.White
        )
    )
}

@Composable
fun IntensityScreenBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(intensityScreenBackdropBrush()),
        content = content
    )
}
