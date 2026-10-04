package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.FavoritePlace
import com.example.model.AppLanguage
import com.example.model.LocationPoint
import com.example.service.LocationTracker

@Composable
fun GoogleMapsBottomSheet(
    destination: LocationPoint?,
    alertRadiusMeters: Int,
    onRadiusChange: (Int) -> Unit,
    userDistanceMeters: Float,
    isTripActive: Boolean,
    onStartTrip: () -> Unit,
    onStopTrip: () -> Unit,
    onClearDestination: () -> Unit,
    onFocusDestinationOnMap: () -> Unit,
    onSaveToFavorites: () -> Unit,
    favorites: List<FavoritePlace>,
    onSelectFavorite: (FavoritePlace) -> Unit,
    onOpenFavoritesManager: () -> Unit,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = Color(0xFF0F1426),
        border = BorderStroke(1.dp, Color(0xFF1F2942)),
        shadowElevation = 18.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle with "Swipe to expand/minimize map" label
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onToggleExpand)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF334155))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "اسحب لتكبير الخريطة"
                            AppLanguage.EN -> "Swipe to expand map"
                            AppLanguage.FR -> "Glisser pour agrandir la carte"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 1: Destination Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Click on map hint
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.AR -> "انقر على الخريطة 📍"
                        AppLanguage.EN -> "Tap on map 📍"
                        AppLanguage.FR -> "Cliquer sur la carte 📍"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )

                // Title + Step 1 Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "وجهة الوصول (المحطة / المكان)"
                            AppLanguage.EN -> "Destination (Station / Place)"
                            AppLanguage.FR -> "Destination (Gare / Arrêt)"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Step 1 Badge
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFF43F5E), Color(0xFFEA580C))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // Destination Card (Matching Screenshot 2)
            if (destination != null) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B30)),
                    border = BorderStroke(1.2.dp, Color(0xFFE11D48).copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("destination_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Clear X button
                            IconButton(
                                onClick = onClearDestination,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Name & Coordinates
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(4f)
                            ) {
                                Text(
                                    text = destination.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = String.format("%.5f , %.5f", destination.longitude, destination.latitude),
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.End
                                )
                                // Distance badge in cyan
                                if (userDistanceMeters < Float.MAX_VALUE && userDistanceMeters > 0f) {
                                    val distStr = if (userDistanceMeters >= 1000f) {
                                        String.format("km %.1f", userDistanceMeters / 1000f)
                                    } else {
                                        "${userDistanceMeters.toInt()} m"
                                    }
                                    Text(
                                        text = distStr,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF00E5FF),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Red pin container
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFEA4335), Color(0xFFF97316))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions Row: "تركيز على الخريطة" and "محفوظ في المفضلة ★"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Focus on Map
                            OutlinedButton(
                                onClick = onFocusDestinationOnMap,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFF2C3960)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFF1B223C),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_focus_on_map")
                            ) {
                                Text(
                                    text = when (currentLanguage) {
                                        AppLanguage.AR -> "تركيز على الخريطة"
                                        AppLanguage.EN -> "Focus on map"
                                        AppLanguage.FR -> "Centrer sur la carte"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Save to Favorites button
                            OutlinedButton(
                                onClick = onSaveToFavorites,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFF242236),
                                    contentColor = Color(0xFFFBBF24)
                                ),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(44.dp)
                                    .testTag("btn_save_favorite")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = when (currentLanguage) {
                                            AppLanguage.AR -> "محفوظ في المفضلة ★"
                                            AppLanguage.EN -> "Saved in Favorites ★"
                                            AppLanguage.FR -> "Sauvegarder ★"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Empty destination prompt
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B30)),
                    border = BorderStroke(1.dp, Color(0xFF283256)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "ابحث عن محطة في الأعلى أو انقر مباشرة على الخريطة"
                                AppLanguage.EN -> "Search station above or tap directly on the map"
                                AppLanguage.FR -> "Recherchez une station ou touchez directement la carte"
                            },
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Quick Favorites Row (Matching Screenshot 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Manage favorites link
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.AR -> "إدارة القائمة"
                        AppLanguage.EN -> "Manage list"
                        AppLanguage.FR -> "Gérer la liste"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF60A5FA),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable(onClick = onOpenFavoritesManager)
                        .padding(4.dp)
                )

                // Label: الأماكن المفضلة المسجلة ★
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.AR -> "الأماكن المفضلة المسجلة ★"
                        AppLanguage.EN -> "Saved Favorite Places ★"
                        AppLanguage.FR -> "Lieux favoris enregistrés ★"
                    },
                    fontSize = 13.sp,
                    color = Color(0xFFFBBF24),
                    fontWeight = FontWeight.Bold
                )
            }

            // Favorite chips scroll
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(favorites) { fav ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1A223B),
                        border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectFavorite(fav) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "★ ${fav.name}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 2: Pre-alert Distance (Matching Screenshot 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Distance badge (e.g. 500 m in cyan)
                Text(
                    text = formatDistance(alertRadiusMeters),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF)
                )

                // Title + Step 2 Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "مسافة التنبيه المسبق"
                            AppLanguage.EN -> "Pre-alert Distance"
                            AppLanguage.FR -> "Distance de réveil anticipé"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // Distance Card with Presets & Slider
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B30)),
                border = BorderStroke(1.dp, Color(0xFF283256)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Quick Preset Chips [200m] [500m] [1km] [2km]
                    val presets = listOf(200, 500, 1000, 2000)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { dist ->
                            val isSelected = alertRadiusMeters == dist
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFF1E243A),
                                border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF2E3858)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onRadiusChange(dist) }
                                    .testTag("preset_$dist")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = formatDistance(dist),
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slider
                    Slider(
                        value = alertRadiusMeters.toFloat(),
                        onValueChange = { onRadiusChange(it.toInt()) },
                        valueRange = 100f..5000f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF00E5FF),
                            inactiveTrackColor = Color(0xFF252D48)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("radius_slider")
                    )

                    // Labels below slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "5 كم (قطار سريع)"
                                AppLanguage.EN -> "5 km (Express Train)"
                                AppLanguage.FR -> "5 km (Train rapide)"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "100 م (حافلة / ترام)"
                                AppLanguage.EN -> "100 m (Bus / Tram)"
                                AppLanguage.FR -> "100 m (Bus / Tram)"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bell Notification Note (Matching Screenshot 2)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF101524))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "🔔 سيرن المنبه عندما تصبح على مسافة ${formatDistance(alertRadiusMeters)} من وجهتك."
                                AppLanguage.EN -> "🔔 The alarm will sound when you are ${formatDistance(alertRadiusMeters)} from destination."
                                AppLanguage.FR -> "🔔 Le réveil sonnera lorsque vous serez à ${formatDistance(alertRadiusMeters)} du lieu."
                            },
                            fontSize = 12.sp,
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Gradient CTA Button (Matching Screenshot 2)
            Button(
                onClick = {
                    if (isTripActive) onStopTrip() else onStartTrip()
                },
                enabled = destination != null || isTripActive,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color(0xFF1E243A)
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isTripActive) {
                            Brush.horizontalGradient(
                                listOf(Color(0xFFE11D48), Color(0xFFBE123C))
                            )
                        } else if (destination != null) {
                            Brush.horizontalGradient(
                                listOf(Color(0xFFE11D48), Color(0xFF8B5CF6))
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(Color(0xFF334155), Color(0xFF1E293B))
                            )
                        }
                    )
                    .testTag("btn_main_action")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isTripActive) {
                            when (currentLanguage) {
                                AppLanguage.AR -> "إيقاف تتبع الرحلة"
                                AppLanguage.EN -> "Stop Trip & Alarm"
                                AppLanguage.FR -> "Arrêter le trajet & alarme"
                            }
                        } else {
                            when (currentLanguage) {
                                AppLanguage.AR -> "بدء تتبع الرحلة والتنبيه"
                                AppLanguage.EN -> "Start Trip & Alarm"
                                AppLanguage.FR -> "Démarrer le trajet & réveil"
                            }
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isTripActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Sponsored Banner (Matching Screenshot 1 & 2)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF14192D),
                border = BorderStroke(1.dp, Color(0xFF222B48)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFBBF24))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "إعلان ممول",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Text(
                        text = "شريك معتمد • ARRIVA GPS 2026",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Text(
                        text = "AD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

fun formatDistance(meters: Int): String {
    return if (meters >= 1000) {
        if (meters % 1000 == 0) "${meters / 1000} km" else String.format("%.1f km", meters / 1000f)
    } else {
        "$meters m"
    }
}

fun formatDistance(meters: Float): String {
    if (meters == Float.MAX_VALUE || meters < 0f) return "--"
    return formatDistance(meters.toInt())
}
