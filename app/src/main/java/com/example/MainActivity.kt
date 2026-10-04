package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.LocationPoint
import com.example.ui.MainViewModel
import com.example.ui.components.AddFavoriteDialog
import com.example.ui.components.AlarmOverlay
import com.example.ui.components.BottomSheetPanel
import com.example.ui.components.FavoritesDialog
import com.example.ui.components.TopBarAndSearch
import com.example.ui.components.TripHud
import com.example.ui.map.ArrivaMapView
import com.example.ui.theme.ArrivaTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val language by viewModel.language.collectAsStateWithLifecycle()
            val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                ArrivaTheme(darkTheme = true) {
                    ArrivaAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ArrivaAppScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Request Location Permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.locationTracker.startRealLocationUpdates()
        }
    }

    LaunchedEffect(Unit) {
        val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (fineCheck == PackageManager.PERMISSION_GRANTED) {
            viewModel.locationTracker.startRealLocationUpdates()
        } else {
            val perms = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                perms.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionLauncher.launch(perms.toTypedArray())
        }
    }

    // States from ViewModel
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val destination by viewModel.destination.collectAsStateWithLifecycle()
    val alertRadius by viewModel.alertRadius.collectAsStateWithLifecycle()
    val alarmTone by viewModel.alarmTone.collectAsStateWithLifecycle()
    val mapStyle by viewModel.mapStyle.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsStateWithLifecycle()
    val isTestingTone by viewModel.isTestingTone.collectAsStateWithLifecycle()
    val isSimulationMode by viewModel.isSimulationMode.collectAsStateWithLifecycle()
    val tripState by viewModel.tripState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    var isFavoritesManagerOpen by remember { mutableStateOf(false) }
    var isAddFavoriteDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Layer 1: Interactive Full-Screen Leaflet Map
            ArrivaMapView(
                userLocation = userLocation,
                destination = destination,
                alertRadiusMeters = alertRadius,
                mapStyle = mapStyle,
                isDarkTheme = true,
                onMapClick = { lat, lng ->
                    viewModel.setMapClickedPoint(lat, lng)
                }
            )

            // Layer 2: Floating Controls over Map
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Top Header, Language, Map style & Search Input
                TopBarAndSearch(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    searchResults = searchResults,
                    isSearching = isSearching,
                    onSelectPlace = { place ->
                        viewModel.setDestination(place)
                        viewModel.onSearchQueryChanged("")
                    },
                    currentLanguage = language,
                    onLanguageChange = viewModel::setLanguage,
                    currentMapStyle = mapStyle,
                    onMapStyleChange = viewModel::setMapStyle,
                    favorites = favorites,
                    onSelectFavorite = { fav ->
                        viewModel.setDestination(
                            LocationPoint(
                                name = fav.name,
                                address = fav.address,
                                latitude = fav.latitude,
                                longitude = fav.longitude
                            )
                        )
                        viewModel.setAlertRadius(fav.defaultRadiusMeters)
                    },
                    onOpenFavoritesManager = { isFavoritesManagerOpen = true }
                )

                // Trip Active Floating HUD
                TripHud(
                    tripState = tripState,
                    userLocation = userLocation,
                    currentLanguage = language,
                    onStopTrip = viewModel::stopTrip
                )

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Configuration & Actions Sheet
                BottomSheetPanel(
                    destination = destination,
                    alertRadiusMeters = alertRadius,
                    onRadiusChange = viewModel::setAlertRadius,
                    selectedTone = alarmTone,
                    onToneChange = viewModel::setAlarmTone,
                    isTestingTone = isTestingTone,
                    onTestToneToggle = viewModel::testToneToggle,
                    isVibrationEnabled = isVibrationEnabled,
                    onVibrationToggle = viewModel::setVibrationEnabled,
                    isSimulationMode = isSimulationMode,
                    onSimulationToggle = viewModel::setSimulationMode,
                    isTripActive = tripState.isActive,
                    onStartTrip = viewModel::startTrip,
                    onStopTrip = viewModel::stopTrip,
                    onClearDestination = viewModel::clearDestination,
                    onSaveToFavorites = { isAddFavoriteDialogOpen = true },
                    currentLanguage = language
                )
            }

            // Layer 3: High Priority Urgent Alarm Overlay (Triggered upon reaching geofence perimeter)
            AlarmOverlay(
                tripState = tripState,
                currentLanguage = language,
                onDismissAlarm = viewModel::dismissAlarm,
                onMuteToggle = viewModel::toggleMute
            )

            // Dialog: Favorites List & Management
            FavoritesDialog(
                isOpen = isFavoritesManagerOpen,
                onDismiss = { isFavoritesManagerOpen = false },
                favorites = favorites,
                onSelectFavorite = { fav ->
                    viewModel.setDestination(
                        LocationPoint(
                            name = fav.name,
                            address = fav.address,
                            latitude = fav.latitude,
                            longitude = fav.longitude
                        )
                    )
                    viewModel.setAlertRadius(fav.defaultRadiusMeters)
                },
                onDeleteFavorite = viewModel::deleteFavorite,
                currentLanguage = language
            )

            // Dialog: Add Current Destination to Favorites
            destination?.let { dest ->
                AddFavoriteDialog(
                    isOpen = isAddFavoriteDialogOpen,
                    onDismiss = { isAddFavoriteDialogOpen = false },
                    initialName = dest.name,
                    initialAddress = dest.address,
                    onConfirm = { name, tag ->
                        viewModel.saveFavorite(name, tag)
                    },
                    currentLanguage = language
                )
            }
        }
    }
}
