package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.LocationPoint
import com.example.model.MapStyle
import com.example.model.UserLocation
import com.example.ui.theme.GlassTokens

@Composable
fun GoogleMapsTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<LocationPoint>,
    isSearching: Boolean,
    onSelectPlace: (LocationPoint) -> Unit,
    currentLanguage: AppLanguage,
    currentMapStyle: MapStyle,
    onMapStyleChange: (MapStyle) -> Unit,
    userLocation: UserLocation,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMapStyleDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Top Header: Hamburger Glass Button + ARRIVA Brand Glass Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Hamburger Menu Glass Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFF6366F1).copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E284A).copy(alpha = 0.75f),
                                Color(0xFF0F1528).copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(onClick = onOpenMenu)
                    .testTag("btn_menu"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // ARRIVA Brand Badge with 1px Glass Border
            Box(
                modifier = Modifier
                    .height(46.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0xFF8B5CF6).copy(alpha = 0.35f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF19223D).copy(alpha = 0.78f),
                                Color(0xFF0C1122).copy(alpha = 0.88f)
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoogleColorsIcon(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "ARRIVA",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "خرائط جوجل • منبه GPS الذكي"
                                AppLanguage.EN -> "Google Maps • Smart GPS Alarm"
                                AppLanguage.FR -> "Google Maps • Alarme GPS Réveil"
                            },
                            fontSize = 9.sp,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Purple navigation glass circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFFD946EF))
                                )
                            )
                            .border(
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = "Navigation",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Floating Glass Search Capsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color(0xFF38BDF8).copy(alpha = 0.25f),
                    ambientColor = Color.Black.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1D2644).copy(alpha = 0.75f),
                            Color(0xFF0E1426).copy(alpha = 0.88f)
                        )
                    )
                )
                .border(
                    BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                    RoundedCornerShape(24.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF818CF8),
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> "ابحث عن محطة، عنوان، موقف أو مكان..."
                                AppLanguage.EN -> "Search station, address, stop or place..."
                                AppLanguage.FR -> "Rechercher gare, adresse, arrêt ou lieu..."
                            },
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("google_search_input")
                )

                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF8B5CF6)
                    )
                } else if (query.isNotBlank()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Sub-filter pill row with Glass Surfaces
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Google Maps Layer selector glass pill
            Box {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFF06B6D4).copy(alpha = 0.2f)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E2848).copy(alpha = 0.72f),
                                    Color(0xFF0F152A).copy(alpha = 0.85f)
                                )
                            )
                        )
                        .border(
                            BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { isMapStyleDropdownOpen = true }
                        .testTag("pill_map_style")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "▾ ", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.AR -> currentMapStyle.labelAr
                                AppLanguage.EN -> currentMapStyle.labelEn
                                AppLanguage.FR -> currentMapStyle.labelFr
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        GoogleColorsIcon(modifier = Modifier.size(12.dp))
                    }
                }

                DropdownMenu(
                    expanded = isMapStyleDropdownOpen,
                    onDismissRequest = { isMapStyleDropdownOpen = false },
                    modifier = Modifier
                        .background(Color(0xFF13182C).copy(alpha = 0.95f))
                        .border(BorderStroke(1.dp, GlassTokens.GlassBorderBrush))
                ) {
                    MapStyle.values().forEach { style ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = when (currentLanguage) {
                                        AppLanguage.AR -> style.labelAr
                                        AppLanguage.EN -> style.labelEn
                                        AppLanguage.FR -> style.labelFr
                                    },
                                    color = if (style == currentMapStyle) Color(0xFF00E5FF) else Color.White
                                )
                            },
                            onClick = {
                                onMapStyleChange(style)
                                isMapStyleDropdownOpen = false
                            }
                        )
                    }
                }
            }

            // GPS Signal Precision glass pill
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color(0xFF10B981).copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E2848).copy(alpha = 0.72f),
                                Color(0xFF0F152A).copy(alpha = 0.85f)
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val accuracyText = if (userLocation.accuracyMeters > 0f) {
                        "±${userLocation.accuracyMeters.toInt()}m"
                    } else "±12m"

                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "إشارة GPS دقيقة ($accuracyText)"
                            AppLanguage.EN -> "Accurate GPS signal ($accuracyText)"
                            AppLanguage.FR -> "Signal GPS précis ($accuracyText)"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Autocomplete Search Results Overlay (Glass Card)
        AnimatedVisibility(
            visible = query.isNotBlank() && searchResults.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .heightIn(max = 240.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color(0xFF4F46E5).copy(alpha = 0.4f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E2746).copy(alpha = 0.90f),
                                Color(0xFF0E1428).copy(alpha = 0.95f)
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.dp, GlassTokens.GlassBorderBrush),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(searchResults) { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectPlace(place) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE11D48).copy(alpha = 0.25f))
                                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFF43F5E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = place.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (place.address.isNotBlank()) {
                                    Text(
                                        text = place.address,
                                        fontSize = 11.sp,
                                        color = Color(0xFFCBD5E1),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleColorsIcon(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF4285F4)))
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFEA4335)))
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFFBBC05)))
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF34A853)))
    }
}
