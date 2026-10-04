package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.FavoritePlace
import com.example.model.AppLanguage
import com.example.model.LocationPoint
import com.example.model.MapStyle
import com.example.ui.theme.ArrivaCyan
import com.example.ui.theme.ArrivaIndigo

@Composable
fun TopBarAndSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<LocationPoint>,
    isSearching: Boolean,
    onSelectPlace: (LocationPoint) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    currentMapStyle: MapStyle,
    onMapStyleChange: (MapStyle) -> Unit,
    favorites: List<FavoritePlace>,
    onSelectFavorite: (FavoritePlace) -> Unit,
    onOpenFavoritesManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    var langMenuExpanded by remember { mutableStateOf(false) }
    var mapMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // App Brand Header with Quick Action Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo & Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(ArrivaIndigo, ArrivaCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "ARRIVA Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ARRIVA",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "منبه الوصول الذكي"
                            AppLanguage.EN -> "Smart GPS Arrival Alarm"
                            AppLanguage.FR -> "Alerte Réveil GPS"
                        },
                        fontSize = 11.sp,
                        color = ArrivaCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Top action buttons: Map style, Language, Favorites
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Map Style Selector Pill
                Box {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .testTag("map_style_button")
                            .clickable { mapMenuExpanded = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Map Style",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.AR -> currentMapStyle.labelAr
                                    AppLanguage.EN -> currentMapStyle.labelEn
                                    AppLanguage.FR -> currentMapStyle.labelFr
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = mapMenuExpanded,
                        onDismissRequest = { mapMenuExpanded = false }
                    ) {
                        MapStyle.values().forEach { style ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (currentLanguage) {
                                            AppLanguage.AR -> style.labelAr
                                            AppLanguage.EN -> style.labelEn
                                            AppLanguage.FR -> style.labelFr
                                        }
                                    )
                                },
                                onClick = {
                                    onMapStyleChange(style)
                                    mapMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Language Selector Pill
                Box {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .testTag("language_selector_button")
                            .clickable { langMenuExpanded = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Language",
                                tint = ArrivaCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLanguage.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = langMenuExpanded,
                        onDismissRequest = { langMenuExpanded = false }
                    ) {
                        AppLanguage.values().forEach { lang ->
                            DropdownMenuItem(
                                text = { Text("${lang.label} (${lang.name})") },
                                onClick = {
                                    onLanguageChange(lang)
                                    langMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Open Favorites Button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .testTag("open_favorites_button")
                        .clickable { onOpenFavoritesManager() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .padding(7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favoris",
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Floating Search Input Bar
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            shadowElevation = 8.dp,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = when (currentLanguage) {
                            AppLanguage.AR -> "ابحث عن محطة، موقف، عنوان..."
                            AppLanguage.EN -> "Search station, stop, address..."
                            AppLanguage.FR -> "Rechercher gare, arrêt, métro, adresse..."
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ArrivaIndigo
                    )
                },
                trailingIcon = {
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = ArrivaIndigo
                        )
                    } else if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_text_input")
            )
        }

        // Search Results Dropdown List
        AnimatedVisibility(
            visible = searchResults.isNotEmpty() && query.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .heightIn(max = 240.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(searchResults) { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectPlace(place)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = place.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (place.address.isNotEmpty()) {
                                    Text(
                                        text = place.address,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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

        // Quick Favorites / Suggested Transit Stops Chips
        if (query.isEmpty() && favorites.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(favorites.take(5)) { fav ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .testTag("quick_fav_${fav.id}")
                            .clickable {
                                onSelectFavorite(fav)
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Train,
                                contentDescription = null,
                                tint = ArrivaIndigo,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = fav.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
