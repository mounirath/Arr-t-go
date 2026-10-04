package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlarmTone
import com.example.model.AppLanguage
import com.example.model.LocationPoint
import com.example.ui.theme.ArrivaCyan
import com.example.ui.theme.ArrivaGreen
import com.example.ui.theme.ArrivaIndigo

@Composable
fun BottomSheetPanel(
    destination: LocationPoint?,
    alertRadiusMeters: Int,
    onRadiusChange: (Int) -> Unit,
    selectedTone: AlarmTone,
    onToneChange: (AlarmTone) -> Unit,
    isTestingTone: Boolean,
    onTestToneToggle: () -> Unit,
    isVibrationEnabled: Boolean,
    onVibrationToggle: (Boolean) -> Unit,
    isSimulationMode: Boolean,
    onSimulationToggle: (Boolean) -> Unit,
    isTripActive: Boolean,
    onStartTrip: () -> Unit,
    onStopTrip: () -> Unit,
    onClearDestination: () -> Unit,
    onSaveToFavorites: () -> Unit,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle pill
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step 1: Destination Summary Card
            if (destination != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF43F5E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsTransit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.AR -> "الوجهة المحددة:"
                                    AppLanguage.EN -> "Selected Destination:"
                                    AppLanguage.FR -> "Arrêt / Destination :"
                                },
                                fontSize = 11.sp,
                                color = ArrivaCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = destination.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (destination.address.isNotEmpty()) {
                                Text(
                                    text = destination.address,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Save to Favorites Icon Button
                        IconButton(
                            onClick = onSaveToFavorites,
                            modifier = Modifier.testTag("save_favorite_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Sauvegarder",
                                tint = Color(0xFFF43F5E)
                            )
                        }

                        // Remove Destination
                        IconButton(
                            onClick = onClearDestination,
                            modifier = Modifier.testTag("clear_destination_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Effacer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Empty Destination Prompt
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "📍 المس أي مكان على الخريطة لتحديده"
                                AppLanguage.EN -> "📍 Tap anywhere on map to set destination"
                                AppLanguage.FR -> "📍 Touchez la carte pour choisir votre arrêt"
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "أو استخدم شريط البحث أعلاه لاختيار محطة قطار أو مترو"
                                AppLanguage.EN -> "Or use the search bar above to pick a station"
                                AppLanguage.FR -> "ou utilisez la barre de recherche ci-dessus"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 2: Pre-alarm alert radius selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.AR -> "مسافة التنبيه المسبق"
                        AppLanguage.EN -> "Wake-up Alert Distance"
                        AppLanguage.FR -> "Rayon d'alerte avant l'arrêt"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = formatDistance(alertRadiusMeters),
                    color = ArrivaIndigo,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Radius Preset Buttons
            val presets = listOf(
                100 to "100m",
                300 to "300m",
                500 to "500m",
                1000 to "1 km",
                2000 to "2 km",
                5000 to "5 km"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presets.forEach { (meters, label) ->
                    val isSelected = alertRadiusMeters == meters
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ArrivaIndigo else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("radius_preset_$meters")
                            .clickable { onRadiusChange(meters) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 7.dp)
                        )
                    }
                }
            }

            // Slider for fine tuning
            Slider(
                value = alertRadiusMeters.toFloat(),
                onValueChange = { onRadiusChange(it.toInt()) },
                valueRange = 50f..5000f,
                colors = SliderDefaults.colors(
                    thumbColor = ArrivaIndigo,
                    activeTrackColor = ArrivaIndigo,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("radius_slider")
            )

            // Dynamic explanation text
            Text(
                text = when (currentLanguage) {
                    AppLanguage.AR -> "🔔 سيرن المنبه عندما تصبح على مسافة ${formatDistance(alertRadiusMeters)} من وجهتك."
                    AppLanguage.EN -> "🔔 The alarm will ring as soon as you are within ${formatDistance(alertRadiusMeters)}."
                    AppLanguage.FR -> "🔔 L'alarme sonnera dès que vous serez à ${formatDistance(alertRadiusMeters)} de l'arrêt."
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step 3: Alarm Tone & Sound Tester
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.AR -> "نغمة المنبه"
                        AppLanguage.EN -> "Alarm Sound Tone"
                        AppLanguage.FR -> "Sonnerie d'alarme"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Sound Tester Button
                OutlinedButton(
                    onClick = onTestToneToggle,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("test_tone_button")
                ) {
                    Icon(
                        imageVector = if (isTestingTone) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isTestingTone) Color(0xFFF43F5E) else ArrivaIndigo
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTestingTone) {
                            when (currentLanguage) {
                                AppLanguage.AR -> "إيقاف"
                                AppLanguage.EN -> "Stop"
                                AppLanguage.FR -> "Arrêter"
                            }
                        } else {
                            when (currentLanguage) {
                                AppLanguage.AR -> "تجربة الصوت"
                                AppLanguage.EN -> "Test Tone"
                                AppLanguage.FR -> "Tester le son"
                            }
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tone Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AlarmTone.values().forEach { tone ->
                    val isSelected = selectedTone == tone
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ArrivaIndigo.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ArrivaIndigo) else null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tone_${tone.id}")
                            .clickable { onToneChange(tone) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (isSelected) ArrivaIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.AR -> tone.labelAr
                                    AppLanguage.EN -> tone.labelEn
                                    AppLanguage.FR -> tone.labelFr
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step 4: Vibration & Simulation Switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = ArrivaCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "الاهتزاز"
                            AppLanguage.EN -> "Vibration"
                            AppLanguage.FR -> "Vibration"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Switch(
                    checked = isVibrationEnabled,
                    onCheckedChange = onVibrationToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = ArrivaCyan, checkedTrackColor = ArrivaCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("vibration_switch")
                )
            }

            // Demo Simulation Mode Switch (Test arrival alarm immediately)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "محاكاة الرحلة (تجربة داخلية)"
                            AppLanguage.EN -> "Demo Simulation Mode"
                            AppLanguage.FR -> "Mode Démo / Trajet simulé"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "يحاكي التقدم نحو المحطة لاختبار المنبه"
                            AppLanguage.EN -> "Simulates motion toward station to test alarm"
                            AppLanguage.FR -> "Simule le trajet vers l'arrêt pour tester"
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isSimulationMode,
                    onCheckedChange = onSimulationToggle,
                    modifier = Modifier.testTag("simulation_switch")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main CTA: Start / Stop Trip
            if (!isTripActive) {
                Button(
                    onClick = onStartTrip,
                    enabled = destination != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (destination != null) {
                                Brush.horizontalGradient(listOf(ArrivaIndigo, ArrivaCyan))
                            } else {
                                Brush.horizontalGradient(listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.5f)))
                            }
                        )
                        .testTag("start_trip_button")
                ) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "🚀 بدء تتبع الرحلة"
                            AppLanguage.EN -> "🚀 Start Trip Tracking"
                            AppLanguage.FR -> "🚀 Démarrer le trajet"
                        },
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onStopTrip,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("stop_trip_button")
                ) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "🛑 إيقاف الرحلة"
                            AppLanguage.EN -> "🛑 Stop Trip"
                            AppLanguage.FR -> "🛑 Arrêter le trajet"
                        },
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

fun formatDistance(meters: Int): String {
    return if (meters >= 1000) {
        val km = meters / 1000.0
        if (meters % 1000 == 0) "${km.toInt()} km" else "%.1f km".format(km)
    } else {
        "$meters m"
    }
}

fun formatDistance(meters: Float): String {
    return if (meters >= 1000f) {
        "%.1f km".format(meters / 1000f)
    } else {
        "${meters.toInt()} m"
    }
}
