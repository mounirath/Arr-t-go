package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.TripState
import com.example.model.UserLocation
import com.example.ui.theme.ArrivaCyan
import com.example.ui.theme.ArrivaGreen
import com.example.ui.theme.ArrivaIndigo

@Composable
fun TripHud(
    tripState: TripState,
    userLocation: UserLocation,
    currentLanguage: AppLanguage,
    onStopTrip: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = tripState.isActive,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 12.dp,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header: Destination name & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Pulsing Live Indicator
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (tripState.isWithinAlertZone) Color(0xFFF43F5E) else ArrivaGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (tripState.isWithinAlertZone) {
                                    when (currentLanguage) {
                                        AppLanguage.AR -> "🚨 في منطقة التنبيه!"
                                        AppLanguage.EN -> "🚨 Inside alert zone!"
                                        AppLanguage.FR -> "🚨 Dans la zone d'alerte !"
                                    }
                                } else {
                                    when (currentLanguage) {
                                        AppLanguage.AR -> "رحلة جارية نحو:"
                                        AppLanguage.EN -> "En route to:"
                                        AppLanguage.FR -> "En route vers :"
                                    }
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tripState.isWithinAlertZone) Color(0xFFF43F5E) else ArrivaCyan
                            )
                            Text(
                                text = tripState.destination?.name ?: "",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Stop trip small button
                    IconButton(
                        onClick = onStopTrip,
                        modifier = Modifier.testTag("hud_stop_trip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Arrêter le trajet",
                            tint = Color(0xFFF43F5E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metrics Row: Remaining Distance, Alert Threshold, Speed, ETA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big Distance Remaining
                    Column {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "المسافة المتبقية"
                                AppLanguage.EN -> "Distance left"
                                AppLanguage.FR -> "Distance restante"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (tripState.currentDistanceMeters < Float.MAX_VALUE) {
                                formatDistance(tripState.currentDistanceMeters)
                            } else "--",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = ArrivaIndigo
                        )
                    }

                    // Distance to Geofence Alert Perimeter
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = ArrivaCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.AR -> "منطقة التنبيه"
                                    AppLanguage.EN -> "Alert zone"
                                    AppLanguage.FR -> "Zone d'alerte"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        val distanceToAlert = (tripState.currentDistanceMeters - tripState.alertRadiusMeters).coerceAtLeast(0f)
                        Text(
                            text = if (tripState.isWithinAlertZone) {
                                when (currentLanguage) {
                                    AppLanguage.AR -> "الآن!"
                                    AppLanguage.EN -> "Now!"
                                    AppLanguage.FR -> "Atteinte !"
                                }
                            } else {
                                "dans ${formatDistance(distanceToAlert)}"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tripState.isWithinAlertZone) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Speed
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = ArrivaGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.AR -> "السرعة"
                                    AppLanguage.EN -> "Speed"
                                    AppLanguage.FR -> "Vitesse"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${userLocation.speedKmh.toInt()} km/h",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar towards destination
                val progress = if (tripState.initialDistanceMeters > 0f && tripState.currentDistanceMeters < Float.MAX_VALUE) {
                    ((tripState.initialDistanceMeters - tripState.currentDistanceMeters) / tripState.initialDistanceMeters).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (tripState.isWithinAlertZone) Color(0xFFF43F5E) else ArrivaIndigo,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}
