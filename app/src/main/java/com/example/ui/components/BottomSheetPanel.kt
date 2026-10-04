package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FavoritePlace
import com.example.model.AppLanguage
import com.example.model.LocationPoint
import com.example.ui.theme.GlassTokens
import kotlin.math.roundToInt

enum class CardSlideState(val targetHeightDp: Int) {
    PEEK(130),
    HALF(380),
    EXPANDED(640)
}

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
    // Draggable Slide State with 3 snap points (Peek, Half, Expanded)
    var slideState by remember { mutableStateOf(CardSlideState.HALF) }
    var currentDragDelta by remember { mutableFloatStateOf(0f) }

    val animatedHeight by animateDpAsState(
        targetValue = slideState.targetHeightDp.dp,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "sheet_height"
    )

    // Glassmorphism Surface Container with Auto Layout
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .offset { IntOffset(0, (currentDragDelta * 0.5f).roundToInt().coerceIn(-120, 120)) }
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                spotColor = Color(0xFF6366F1).copy(alpha = 0.45f),
                ambientColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        GlassTokens.GlassSurfaceTop,
                        GlassTokens.GlassSurfaceBottom
                    )
                )
            )
            .border(
                BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { currentDragDelta = 0f },
                    onDragEnd = {
                        if (currentDragDelta < -40f) {
                            // Dragged Up
                            slideState = when (slideState) {
                                CardSlideState.PEEK -> CardSlideState.HALF
                                CardSlideState.HALF -> CardSlideState.EXPANDED
                                CardSlideState.EXPANDED -> CardSlideState.EXPANDED
                            }
                        } else if (currentDragDelta > 40f) {
                            // Dragged Down
                            slideState = when (slideState) {
                                CardSlideState.EXPANDED -> CardSlideState.HALF
                                CardSlideState.HALF -> CardSlideState.PEEK
                                CardSlideState.PEEK -> CardSlideState.PEEK
                            }
                        }
                        currentDragDelta = 0f
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        currentDragDelta += dragAmount
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Handle Area (Auto Layout Header)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        slideState = when (slideState) {
                            CardSlideState.PEEK -> CardSlideState.HALF
                            CardSlideState.HALF -> CardSlideState.EXPANDED
                            CardSlideState.EXPANDED -> CardSlideState.PEEK
                        }
                    }
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Pill bar
                    Box(
                        modifier = Modifier
                            .width(46.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.40f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (slideState) {
                                CardSlideState.PEEK -> when (currentLanguage) {
                                    AppLanguage.AR -> "اسحب لأعلى لعرض الخيارات ▴"
                                    AppLanguage.EN -> "Swipe up for options ▴"
                                    AppLanguage.FR -> "Glisser vers le haut ▴"
                                }
                                CardSlideState.HALF -> when (currentLanguage) {
                                    AppLanguage.AR -> "اسحب للتكبير أو التصغير"
                                    AppLanguage.EN -> "Swipe to expand / collapse"
                                    AppLanguage.FR -> "Glisser pour agrandir / réduire"
                                }
                                CardSlideState.EXPANDED -> when (currentLanguage) {
                                    AppLanguage.AR -> "اسحب لأسفل لتكبير الخريطة ▾"
                                    AppLanguage.EN -> "Swipe down to view map ▾"
                                    AppLanguage.FR -> "Glisser vers le bas ▾"
                                }
                            },
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SECTION 1: Destination Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFF43F5E), Color(0xFFEA580C))
                                )
                            )
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)), CircleShape),
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

            // Destination Glass Card
            if (destination != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color(0xFFF43F5E).copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    GlassTokens.GlassCardTop,
                                    GlassTokens.GlassCardBottom
                                )
                            )
                        )
                        .border(
                            BorderStroke(1.dp, GlassTokens.GlassCardBorderBrush),
                            RoundedCornerShape(20.dp)
                        )
                        .testTag("destination_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onClearDestination,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

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
                                    color = Color(0xFFCBD5E1),
                                    textAlign = TextAlign.End
                                )
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

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFEA4335), Color(0xFFF97316))
                                        )
                                    )
                                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)), RoundedCornerShape(14.dp)),
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

                        // Focus on Map and Favorite buttons (shown in HALF and EXPANDED states)
                        if (slideState != CardSlideState.PEEK) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.10f))
                                        .border(
                                            BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable(onClick = onFocusDestinationOnMap)
                                        .testTag("btn_focus_on_map"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (currentLanguage) {
                                            AppLanguage.AR -> "تركيز على الخريطة"
                                            AppLanguage.EN -> "Focus on map"
                                            AppLanguage.FR -> "Centrer sur la carte"
                                        },
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFD97706).copy(alpha = 0.20f))
                                        .border(
                                            BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable(onClick = onSaveToFavorites)
                                        .testTag("btn_save_favorite"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (currentLanguage) {
                                            AppLanguage.AR -> "محفوظ في المفضلة ★"
                                            AppLanguage.EN -> "Saved in Favorites ★"
                                            AppLanguage.FR -> "Sauvegarder ★"
                                        },
                                        color = Color(0xFFFBBF24),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Favorites Row (HALF & EXPANDED)
            if (slideState != CardSlideState.PEEK) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "إدارة القائمة"
                            AppLanguage.EN -> "Manage list"
                            AppLanguage.FR -> "Gérer la liste"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF60A5FA),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable(onClick = onOpenFavoritesManager)
                            .padding(4.dp)
                    )

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

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(favorites) { fav ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.10f))
                                .border(
                                    BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { onSelectFavorite(fav) }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
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

            // SECTION 2: Pre-alert Distance (EXPANDED Mode)
            if (slideState == CardSlideState.EXPANDED) {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDistance(alertRadiusMeters),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF)
                    )

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
                                )
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)), CircleShape),
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

                // Distance Glass Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color(0xFF0284C7).copy(alpha = 0.25f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    GlassTokens.GlassCardTop,
                                    GlassTokens.GlassCardBottom
                                )
                            )
                        )
                        .border(
                            BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        val presets = listOf(200, 500, 1000, 2000)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            presets.forEach { dist ->
                                val isSelected = alertRadiusMeters == dist
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) {
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF0284C7), Color(0xFF06B6D4))
                                                )
                                            } else {
                                                Brush.linearGradient(
                                                    listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.08f))
                                                )
                                            }
                                        )
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                if (isSelected) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f)
                                            ),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onRadiusChange(dist) }
                                        .testTag("preset_$dist"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = formatDistance(dist),
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Slider(
                            value = alertRadiusMeters.toFloat(),
                            onValueChange = { onRadiusChange(it.toInt()) },
                            valueRange = 100f..5000f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color(0xFF00E5FF),
                                inactiveTrackColor = Color.White.copy(alpha = 0.18f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("radius_slider")
                        )

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
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.AR -> "100 م (حافلة / ترام)"
                                    AppLanguage.EN -> "100 m (Bus / Tram)"
                                    AppLanguage.FR -> "100 m (Bus / Tram)"
                                },
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.28f))
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)), RoundedCornerShape(12.dp))
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
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big Gradient CTA Button (Present in HALF & EXPANDED)
            Button(
                onClick = {
                    if (isTripActive) onStopTrip() else onStartTrip()
                },
                enabled = destination != null || isTripActive,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.White.copy(alpha = 0.08f)
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color(0xFFD946EF).copy(alpha = 0.45f)
                    )
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
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)), RoundedCornerShape(20.dp))
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

            // Bottom Sponsored Banner (in EXPANDED)
            if (slideState == CardSlideState.EXPANDED) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(BorderStroke(1.dp, GlassTokens.GlassBorderBrush), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                            color = Color(0xFFCBD5E1)
                        )

                        Text(
                            text = "AD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
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
