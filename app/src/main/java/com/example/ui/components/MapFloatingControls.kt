package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.MapStyle

@Composable
fun MapFloatingControls(
    onCenterLocation: () -> Unit,
    isFullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    currentMapStyle: MapStyle,
    onMapStyleChange: (MapStyle) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    var isLayersMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Center on GPS Location Button
        FloatingMapButton(
            icon = Icons.Default.MyLocation,
            contentDescription = "Center on my location",
            testTag = "btn_center_location",
            tint = Color(0xFF00E5FF),
            onClick = onCenterLocation
        )

        // 2. Fullscreen / Focus Mode Button
        FloatingMapButton(
            icon = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
            contentDescription = "Toggle full map view",
            testTag = "btn_toggle_fullscreen",
            tint = Color.White,
            onClick = onToggleFullscreen
        )

        // 3. Layers / Map Style Button
        Box {
            FloatingMapButton(
                icon = Icons.Default.Layers,
                contentDescription = "Map Style Layers",
                testTag = "btn_map_layers",
                tint = Color.White,
                onClick = { isLayersMenuOpen = true }
            )

            DropdownMenu(
                expanded = isLayersMenuOpen,
                onDismissRequest = { isLayersMenuOpen = false },
                modifier = Modifier.background(Color(0xFF13182C))
            ) {
                MapStyle.values().forEach { style ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (style == currentMapStyle) {
                                    Text("✓ ", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = when (currentLanguage) {
                                        AppLanguage.AR -> style.labelAr
                                        AppLanguage.EN -> style.labelEn
                                        AppLanguage.FR -> style.labelFr
                                    },
                                    color = if (style == currentMapStyle) Color(0xFF00E5FF) else Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            onMapStyleChange(style)
                            isLayersMenuOpen = false
                        }
                    )
                }
            }
        }

        // 4. Zoom In Button (+)
        FloatingMapButton(
            icon = Icons.Default.Add,
            contentDescription = "Zoom in",
            testTag = "btn_zoom_in",
            tint = Color.White,
            onClick = onZoomIn
        )

        // 5. Zoom Out Button (-)
        FloatingMapButton(
            icon = Icons.Default.Remove,
            contentDescription = "Zoom out",
            testTag = "btn_zoom_out",
            tint = Color.White,
            onClick = onZoomOut
        )
    }
}

@Composable
private fun FloatingMapButton(
    icon: ImageVector,
    contentDescription: String,
    testTag: String,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E243A).copy(alpha = 0.94f),
        shadowElevation = 8.dp,
        modifier = Modifier
            .size(46.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
