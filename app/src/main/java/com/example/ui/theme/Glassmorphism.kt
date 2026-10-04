package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standard Glassmorphism Color Palettes & Brushes
 * Defined according to glassmorphism principles:
 * - Semi-transparent background with deep tint
 * - 1px crisp specular edge border with semi-transparent white
 * - Soft layered shadows and specular light reflections
 */
object GlassTokens {
    // Glass Surface Fills (70-80% opacity to let vivid backdrop bleed through)
    val GlassSurfaceTop = Color(0xFF1E2846).copy(alpha = 0.72f)
    val GlassSurfaceBottom = Color(0xFF0F1528).copy(alpha = 0.85f)
    val GlassCardTop = Color(0xFF253055).copy(alpha = 0.68f)
    val GlassCardBottom = Color(0xFF131A32).copy(alpha = 0.82f)

    // Specular 1px Border Brushes (Bright white to subtle cyan/indigo)
    val GlassBorderBrush = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.35f),
            Color.White.copy(alpha = 0.10f),
            Color(0xFF818CF8).copy(alpha = 0.28f),
            Color.White.copy(alpha = 0.20f)
        )
    )

    val GlassCardBorderBrush = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.40f),
            Color(0xFFF43F5E).copy(alpha = 0.25f),
            Color.White.copy(alpha = 0.12f)
        )
    )

    val GlassAccentBorderBrush = Brush.linearGradient(
        listOf(
            Color(0xFF00E5FF).copy(alpha = 0.60f),
            Color.White.copy(alpha = 0.25f),
            Color(0xFF8B5CF6).copy(alpha = 0.40f)
        )
    )

    // Vivid Glowing Backdrop Orbs Colors
    val BackdropCyan = Color(0xFF06B6D4)
    val BackdropPurple = Color(0xFF8B5CF6)
    val BackdropPink = Color(0xFFEC4899)
    val BackdropBlue = Color(0xFF3B82F6)
}

/**
 * Reusable Glassmorphism Container with multi-layered depth,
 * specular edge border, and semi-transparent glass fill.
 */
@Composable
fun GlassmorphicContainer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 16.dp,
    borderBrush: Brush = GlassTokens.GlassBorderBrush,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = Color(0xFF4F46E5).copy(alpha = 0.35f),
                ambientColor = Color.Black.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        GlassTokens.GlassSurfaceTop,
                        GlassTokens.GlassSurfaceBottom
                    )
                )
            )
            .border(
                border = BorderStroke(1.dp, borderBrush),
                shape = shape
            )
    ) {
        // Specular top highlight sheen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}
