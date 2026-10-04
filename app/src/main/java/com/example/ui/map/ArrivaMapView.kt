package com.example.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.LocationPoint
import com.example.model.MapStyle
import com.example.model.UserLocation

interface MapBridgeListener {
    fun onMapClicked(lat: Double, lng: Double)
    fun onMapReady()
}

class MapJsBridge(private val listener: MapBridgeListener) {
    @JavascriptInterface
    fun onMapClicked(lat: Double, lng: Double) {
        listener.onMapClicked(lat, lng)
    }

    @JavascriptInterface
    fun onMapLoaded() {
        listener.onMapReady()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ArrivaMapView(
    userLocation: UserLocation,
    destination: LocationPoint?,
    alertRadiusMeters: Int,
    mapStyle: MapStyle,
    isDarkTheme: Boolean,
    onMapClick: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bridgeListener = remember {
        object : MapBridgeListener {
            override fun onMapClicked(lat: Double, lng: Double) {
                onMapClick(lat, lng)
            }
            override fun onMapReady() {}
        }
    }

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
            addJavascriptInterface(MapJsBridge(bridgeListener), "Android")
            loadDataWithBaseURL("https://arriva.local/", generateMapHtml(isDarkTheme), "text/html", "UTF-8", null)
        }
    }

    // Update User Location Marker in Map
    LaunchedEffect(userLocation.latitude, userLocation.longitude, userLocation.accuracyMeters) {
        val script = "if (window.updateUser) { window.updateUser(${userLocation.latitude}, ${userLocation.longitude}, ${userLocation.accuracyMeters}); }"
        webView.evaluateJavascript(script, null)
    }

    // Update Destination & Alert Radius Circle in Map
    LaunchedEffect(destination?.latitude, destination?.longitude, alertRadiusMeters) {
        val dest = destination
        if (dest != null) {
            val script = "if (window.setDestination) { window.setDestination(${dest.latitude}, ${dest.longitude}, '${dest.name.replace("'", "\\'")}', $alertRadiusMeters); }"
            webView.evaluateJavascript(script, null)
        } else {
            val script = "if (window.clearDestination) { window.clearDestination(); }"
            webView.evaluateJavascript(script, null)
        }
    }

    // Update Map Tile Layer
    LaunchedEffect(mapStyle) {
        val script = "if (window.setTileLayer) { window.setTileLayer('${mapStyle.id}'); }"
        webView.evaluateJavascript(script, null)
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}

private fun generateMapHtml(isDarkTheme: Boolean): String {
    val defaultTile = if (isDarkTheme) "dark" else "street"
    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        * { margin:0; padding:0; box-sizing:border-box; -webkit-tap-highlight-color: transparent; }
        html, body, #map { width:100%; height:100%; background:#070b18; overflow:hidden; }
        .leaflet-control-attribution, .leaflet-control-zoom { display:none !important; }
        
        /* Pulse Radar User Marker */
        .user-pulse-container {
            position: relative;
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .user-radar-ring {
            position: absolute;
            width: 44px;
            height: 44px;
            border-radius: 50%;
            background: rgba(34, 211, 238, 0.25);
            border: 2px solid rgba(34, 211, 238, 0.6);
            animation: radarPulse 2s ease-out infinite;
        }
        .user-center-dot {
            width: 16px;
            height: 16px;
            border-radius: 50%;
            background: linear-gradient(135deg, #22d3ee, #6366f1);
            border: 3px solid #ffffff;
            box-shadow: 0 0 14px rgba(34, 211, 238, 0.9);
            z-index: 2;
        }
        @keyframes radarPulse {
            0% { transform: scale(0.6); opacity: 1; }
            100% { transform: scale(1.6); opacity: 0; }
        }

        /* Bouncing Destination Pin */
        .dest-pin-container {
            width: 40px;
            height: 48px;
            display: flex;
            flex-direction: column;
            align-items: center;
            animation: bouncePin 2s infinite ease-in-out;
            transform-origin: bottom center;
            filter: drop-shadow(0 6px 12px rgba(244, 63, 94, 0.6));
        }
        @keyframes bouncePin {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-8px); }
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map = L.map('map', {
            center: [48.8566, 2.3522],
            zoom: 14,
            zoomControl: false,
            attributionControl: false
        });

        var tileLayers = {
            dark: L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', { maxZoom: 19 }),
            street: L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }),
            satellite: L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', { maxZoom: 18 })
        };

        var currentLayer = tileLayers['$defaultTile'] || tileLayers.dark;
        currentLayer.addTo(map);

        window.setTileLayer = function(styleId) {
            if (currentLayer) map.removeLayer(currentLayer);
            currentLayer = tileLayers[styleId] || tileLayers.dark;
            currentLayer.addTo(map);
        };

        // User Marker
        var userIcon = L.divIcon({
            className: 'user-icon-leaflet',
            html: '<div class="user-pulse-container"><div class="user-radar-ring"></div><div class="user-center-dot"></div></div>',
            iconSize: [32, 32],
            iconAnchor: [16, 16]
        });
        var userMarker = null;
        var userCircle = null;

        window.updateUser = function(lat, lng, accuracy) {
            if (!userMarker) {
                userMarker = L.marker([lat, lng], { icon: userIcon }).addTo(map);
                map.setView([lat, lng], 15);
            } else {
                userMarker.setLatLng([lat, lng]);
            }
            updateRoute();
        };

        // Destination Marker & Geofence Circle
        var destPinSvg = '<svg width="36" height="46" viewBox="0 0 36 46" fill="none" xmlns="http://www.w3.org/2000/svg">' +
            '<path d="M18 0C8.06 0 0 8.06 0 18C0 31.5 18 46 18 46C18 46 36 31.5 36 18C36 8.06 27.94 0 18 0Z" fill="url(#paint0_linear)"/>' +
            '<circle cx="18" cy="18" r="7" fill="white"/>' +
            '<defs><linearGradient id="paint0_linear" x1="0" y1="0" x2="36" y2="46" gradientUnits="userSpaceOnUse">' +
            '<stop stop-color="#F43F5E"/><stop offset="1" stop-color="#BE123C"/></linearGradient></defs></svg>';

        var destIcon = L.divIcon({
            className: 'dest-icon-leaflet',
            html: '<div class="dest-pin-container">' + destPinSvg + '</div>',
            iconSize: [36, 46],
            iconAnchor: [18, 44]
        });

        var destMarker = null;
        var geofenceCircle = null;
        var routePolyline = null;
        var currentDestLatLng = null;

        window.setDestination = function(lat, lng, name, radius) {
            currentDestLatLng = [lat, lng];
            if (!destMarker) {
                destMarker = L.marker([lat, lng], { icon: destIcon }).addTo(map);
            } else {
                destMarker.setLatLng([lat, lng]);
            }

            if (!geofenceCircle) {
                geofenceCircle = L.circle([lat, lng], {
                    radius: radius,
                    color: '#f43f5e',
                    weight: 2,
                    dashArray: '6, 6',
                    fillColor: '#f43f5e',
                    fillOpacity: 0.18
                }).addTo(map);
            } else {
                geofenceCircle.setLatLng([lat, lng]);
                geofenceCircle.setRadius(radius);
            }

            updateRoute();
            fitBoth();
        };

        window.clearDestination = function() {
            if (destMarker) { map.removeLayer(destMarker); destMarker = null; }
            if (geofenceCircle) { map.removeLayer(geofenceCircle); geofenceCircle = null; }
            if (routePolyline) { map.removeLayer(routePolyline); routePolyline = null; }
            currentDestLatLng = null;
        };

        function updateRoute() {
            if (userMarker && destMarker) {
                var userLatLng = userMarker.getLatLng();
                var destLatLng = destMarker.getLatLng();
                var points = [userLatLng, destLatLng];
                if (!routePolyline) {
                    routePolyline = L.polyline(points, {
                        color: '#6366f1',
                        weight: 3,
                        dashArray: '4, 8',
                        opacity: 0.8
                    }).addTo(map);
                } else {
                    routePolyline.setLatLngs(points);
                }
            }
        }

        function fitBoth() {
            if (userMarker && destMarker) {
                var group = new L.featureGroup([userMarker, destMarker, geofenceCircle]);
                map.fitBounds(group.getBounds().pad(0.3));
            } else if (destMarker) {
                map.setView(destMarker.getLatLng(), 15);
            }
        }

        // Tap to drop pin
        map.on('click', function(e) {
            if (window.Android && window.Android.onMapClicked) {
                window.Android.onMapClicked(e.latlng.lat, e.latlng.lng);
            }
        });

        // Inform Android that map is ready
        setTimeout(function() {
            if (window.Android && window.Android.onMapLoaded) {
                window.Android.onMapLoaded();
            }
        }, 500);
    </script>
</body>
</html>
    """.trimIndent()
}
