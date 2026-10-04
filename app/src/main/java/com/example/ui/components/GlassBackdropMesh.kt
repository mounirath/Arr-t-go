package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.GlassTokens

/**
 * Vivid Chromatic Backdrop Mesh
 * Renders glowing ambient radial gradients that bleed vibrant colors (Cyan, Purple, Magenta, Blue)
 * through every semi-transparent glassmorphic panel in the UI.
 */
@Composable
fun GlassBackdropMesh(
    modifier: Modifier = Modifier,
    alpha: Float = 0.55f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "backdrop_animation")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(7500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Orb 1: Top Right Vivid Purple / Indigo Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlassTokens.BackdropPurple.copy(alpha = 0.45f * alpha),
                    GlassTokens.BackdropBlue.copy(alpha = 0.20f * alpha),
                    Color.Transparent
                ),
                center = Offset(w * 0.85f, h * 0.15f),
                radius = w * 0.65f * pulse1
            ),
            radius = w * 0.65f * pulse1,
            center = Offset(w * 0.85f, h * 0.15f)
        )

        // Orb 2: Center Left Electric Cyan Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlassTokens.BackdropCyan.copy(alpha = 0.40f * alpha),
                    GlassTokens.BackdropBlue.copy(alpha = 0.15f * alpha),
                    Color.Transparent
                ),
                center = Offset(w * 0.12f, h * 0.45f),
                radius = w * 0.55f * pulse2
            ),
            radius = w * 0.55f * pulse2,
            center = Offset(w * 0.12f, h * 0.45f)
        )

        // Orb 3: Bottom Right Vivid Magenta / Rose Glow (Bleeding through Bottom Sheet!)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlassTokens.BackdropPink.copy(alpha = 0.45f * alpha),
                    GlassTokens.BackdropPurple.copy(alpha = 0.25f * alpha),
                    Color.Transparent
                ),
                center = Offset(w * 0.70f, h * 0.82f),
                radius = w * 0.70f * pulse1
            ),
            radius = w * 0.70f * pulse1,
            center = Offset(w * 0.70f, h * 0.82f)
        )

        // Orb 4: Bottom Left Saturated Cyan / Teal Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00E5FF).copy(alpha = 0.35f * alpha),
                    Color(0xFF3B82F6).copy(alpha = 0.15f * alpha),
                    Color.Transparent
                ),
                center = Offset(w * 0.25f, h * 0.90f),
                radius = w * 0.60f * pulse2
            ),
            radius = w * 0.60f * pulse2,
            center = Offset(w * 0.25f, h * 0.90f)
        )
    }
}
