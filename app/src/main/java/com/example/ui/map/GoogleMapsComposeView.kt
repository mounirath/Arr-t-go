package com.example.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.LocationPoint
import com.example.model.MapStyle
import com.example.model.UserLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun GoogleMapsComposeView(
    userLocation: UserLocation,
    destination: LocationPoint?,
    alertRadiusMeters: Int,
    mapStyle: MapStyle,
    onMapClick: (Double, Double) -> Unit,
    centerUserTrigger: Long = 0L,
    centerDestTrigger: Long = 0L,
    zoomInTrigger: Long = 0L,
    zoomOutTrigger: Long = 0L,
    modifier: Modifier = Modifier
) {
    val initialPos = LatLng(userLocation.latitude, userLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 14.5f)
    }

    val mapType = when (mapStyle) {
        MapStyle.GOOGLE_MAPS -> MapType.NORMAL
        MapStyle.SATELLITE -> MapType.HYBRID
        MapStyle.TERRAIN -> MapType.TERRAIN
        MapStyle.DARK -> MapType.NORMAL
    }

    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
            compassEnabled = true
        )
    }

    val properties = remember(mapType) {
        MapProperties(
            mapType = mapType,
            isMyLocationEnabled = false
        )
    }

    // Camera actions
    LaunchedEffect(centerUserTrigger) {
        if (centerUserTrigger > 0L) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(userLocation.latitude, userLocation.longitude),
                    15.5f
                )
            )
        }
    }

    LaunchedEffect(centerDestTrigger) {
        if (centerDestTrigger > 0L && destination != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(destination.latitude, destination.longitude),
                    15.5f
                )
            )
        }
    }

    LaunchedEffect(zoomInTrigger) {
        if (zoomInTrigger > 0L) {
            cameraPositionState.animate(CameraUpdateFactory.zoomIn())
        }
    }

    LaunchedEffect(zoomOutTrigger) {
        if (zoomOutTrigger > 0L) {
            cameraPositionState.animate(CameraUpdateFactory.zoomOut())
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = { latLng ->
            onMapClick(latLng.latitude, latLng.longitude)
        }
    ) {
        // User Location Marker
        Marker(
            state = MarkerState(position = LatLng(userLocation.latitude, userLocation.longitude)),
            title = "Votre position",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )

        // Destination Marker & Geofence Circle
        if (destination != null) {
            val destLatLng = LatLng(destination.latitude, destination.longitude)

            Marker(
                state = MarkerState(position = destLatLng),
                title = destination.name,
                snippet = "Rayon de réveil: $alertRadiusMeters m",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )

            Circle(
                center = destLatLng,
                radius = alertRadiusMeters.toDouble(),
                fillColor = Color(0x3000E5FF),
                strokeColor = Color(0xFF00E5FF),
                strokeWidth = 4f
            )

            // Polyline connecting User and Destination
            Polyline(
                points = listOf(
                    LatLng(userLocation.latitude, userLocation.longitude),
                    destLatLng
                ),
                color = Color(0xFF1A73E8),
                width = 8f
            )
        }
    }
}
