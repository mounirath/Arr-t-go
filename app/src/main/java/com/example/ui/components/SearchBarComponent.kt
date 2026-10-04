package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Train
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FavoritePlace
import com.example.model.AppLanguage
import com.example.model.LocationPoint
import com.example.model.MapStyle
import com.example.model.UserLocation

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
        // Top Header: Hamburger Menu + ARRIVA Google Maps Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Hamburger Menu Button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1E243A).copy(alpha = 0.94f),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenMenu)
                    .testTag("btn_menu")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ARRIVA Brand Badge with Google dot & Navigation Icon
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF13182C).copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Color(0xFF283256)),
                shadowElevation = 6.dp,
                modifier = Modifier.height(46.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Google 4-Color Icon Dot
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
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Purple circular navigation pill icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFFD946EF))
                                )
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

        // Floating Search Capsule
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF13182C).copy(alpha = 0.96f),
            border = BorderStroke(1.dp, Color(0xFF2A345A)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
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
                    tint = Color(0xFF8B5CF6),
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
                            color = Color(0xFF8E9BB5),
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
                        modifier = Modifier.size(24.dp)
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

        // Sub-filter pill row: [Google Maps ▾] and [GPS Accuracy Signal]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Google Maps Layer selector pill
            Box {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF192038).copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, Color(0xFF2C3960)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isMapStyleDropdownOpen = true }
                        .testTag("pill_map_style")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        GoogleColorsIcon(modifier = Modifier.size(12.dp))
                    }
                }

                DropdownMenu(
                    expanded = isMapStyleDropdownOpen,
                    onDismissRequest = { isMapStyleDropdownOpen = false },
                    modifier = Modifier.background(Color(0xFF13182C))
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

            // GPS Signal Precision pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF192038).copy(alpha = 0.92f),
                border = BorderStroke(1.dp, Color(0xFF2C3960))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)) // Green active dot
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
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Autocomplete Search Results Overlay
        AnimatedVisibility(
            visible = query.isNotBlank() && searchResults.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF13182C).copy(alpha = 0.98f),
                border = BorderStroke(1.dp, Color(0xFF2C3960)),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .heightIn(max = 240.dp)
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE11D48).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFE11D48),
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
                                        color = Color(0xFF94A3B8),
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
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF4285F4))) // Blue
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFEA4335))) // Red
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFFBBC05))) // Yellow
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF34A853))) // Green
    }
}
