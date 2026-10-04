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
    onMapClick: (Double, Double) -> Unit,
    centerUserTrigger: Long = 0L,
    centerDestTrigger: Long = 0L,
    zoomInTrigger: Long = 0L,
    zoomOutTrigger: Long = 0L,
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
            loadDataWithBaseURL("https://arriva.local/", generateMapHtml(mapStyle.id), "text/html", "UTF-8", null)
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

    // Action Triggers
    LaunchedEffect(centerUserTrigger) {
        if (centerUserTrigger > 0L) {
            webView.evaluateJavascript("if (window.centerOnUser) { window.centerOnUser(); }", null)
        }
    }

    LaunchedEffect(centerDestTrigger) {
        if (centerDestTrigger > 0L) {
            webView.evaluateJavascript("if (window.centerOnDest) { window.centerOnDest(); }", null)
        }
    }

    LaunchedEffect(zoomInTrigger) {
        if (zoomInTrigger > 0L) {
            webView.evaluateJavascript("if (window.zoomIn) { window.zoomIn(); }", null)
        }
    }

    LaunchedEffect(zoomOutTrigger) {
        if (zoomOutTrigger > 0L) {
            webView.evaluateJavascript("if (window.zoomOut) { window.zoomOut(); }", null)
        }
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

private fun generateMapHtml(initialStyle: String): String {
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
        html, body, #map { width:100%; height:100%; background:#e5e3df; overflow:hidden; }
        .leaflet-control-attribution, .leaflet-control-zoom { display:none !important; }
        
        /* Google Maps Accurate Pulse Marker */
        .user-pulse-container {
            position: relative;
            width: 48px;
            height: 48px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .user-radar-ring {
            position: absolute;
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background: rgba(66, 133, 244, 0.22);
            border: 1.5px solid rgba(66, 133, 244, 0.65);
            animation: radarPulse 2.2s ease-out infinite;
        }
        .user-center-dot {
            width: 18px;
            height: 18px;
            border-radius: 50%;
            background: #1a73e8;
            border: 3px solid #ffffff;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.35);
            z-index: 2;
        }
        @keyframes radarPulse {
            0% { transform: scale(0.5); opacity: 1; }
            100% { transform: scale(1.6); opacity: 0; }
        }

        /* Google Maps Bouncing Red Destination Pin */
        .dest-pin-container {
            width: 44px;
            height: 52px;
            display: flex;
            flex-direction: column;
            align-items: center;
            animation: bouncePin 2s infinite ease-in-out;
            transform-origin: bottom center;
            filter: drop-shadow(0 6px 10px rgba(0, 0, 0, 0.35));
        }
        @keyframes bouncePin {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-7px); }
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map = L.map('map', {
            center: [36.7538, 3.0588], // Default Algiers / City view matching screenshots
            zoom: 14,
            zoomControl: false,
            attributionControl: false
        });

        // Google Maps & Complementary High-Quality Tiles
        var tileLayers = {
            google: L.tileLayer('https://mt1.google.com/vt/lyrs=m&x={x}&y={y}&z={z}', {
                maxZoom: 20,
                subdomains: ['mt0', 'mt1', 'mt2', 'mt3']
            }),
            satellite: L.tileLayer('https://mt1.google.com/vt/lyrs=y&x={x}&y={y}&z={z}', {
                maxZoom: 20,
                subdomains: ['mt0', 'mt1', 'mt2', 'mt3']
            }),
            terrain: L.tileLayer('https://mt1.google.com/vt/lyrs=p&x={x}&y={y}&z={z}', {
                maxZoom: 20,
                subdomains: ['mt0', 'mt1', 'mt2', 'mt3']
            }),
            dark: L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
                maxZoom: 19
            })
        };

        var currentStyleKey = '$initialStyle' || 'google';
        var currentLayer = tileLayers[currentStyleKey] || tileLayers.google;
        currentLayer.addTo(map);

        window.setTileLayer = function(styleId) {
            if (currentLayer) map.removeLayer(currentLayer);
            currentLayer = tileLayers[styleId] || tileLayers.google;
            currentLayer.addTo(map);
        };

        // User Marker
        var userIcon = L.divIcon({
            className: 'user-icon-leaflet',
            html: '<div class="user-pulse-container"><div class="user-radar-ring"></div><div class="user-center-dot"></div></div>',
            iconSize: [48, 48],
            iconAnchor: [24, 24]
        });
        var userMarker = null;

        window.updateUser = function(lat, lng, accuracy) {
            if (!userMarker) {
                userMarker = L.marker([lat, lng], { icon: userIcon }).addTo(map);
                map.setView([lat, lng], 15);
            } else {
                userMarker.setLatLng([lat, lng]);
            }
            updateRoute();
        };

        // Destination Marker (Google Maps Red Pin SVG)
        var destPinSvg = '<svg width="40" height="48" viewBox="0 0 40 48" fill="none" xmlns="http://www.w3.org/2000/svg">' +
            '<path d="M20 0C8.954 0 0 8.954 0 20C0 35 20 48 20 48C20 48 40 35 40 20C40 8.954 31.046 0 20 0Z" fill="#EA4335"/>' +
            '<circle cx="20" cy="18" r="8" fill="white"/>' +
            '<circle cx="20" cy="18" r="4" fill="#B31412"/>' +
            '</svg>';

        var destIcon = L.divIcon({
            className: 'dest-icon-leaflet',
            html: '<div class="dest-pin-container">' + destPinSvg + '</div>',
            iconSize: [40, 48],
            iconAnchor: [20, 46]
        });

        var destMarker = null;
        var geofenceCircle = null;
        var routePolyline = null;

        window.setDestination = function(lat, lng, name, radius) {
            if (!destMarker) {
                destMarker = L.marker([lat, lng], { icon: destIcon }).addTo(map);
            } else {
                destMarker.setLatLng([lat, lng]);
            }

            if (!geofenceCircle) {
                geofenceCircle = L.circle([lat, lng], {
                    radius: radius,
                    color: '#EA4335',
                    weight: 2,
                    dashArray: '5, 5',
                    fillColor: '#EA4335',
                    fillOpacity: 0.16
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
        };

        function updateRoute() {
            if (userMarker && destMarker) {
                var userLatLng = userMarker.getLatLng();
                var destLatLng = destMarker.getLatLng();
                var points = [userLatLng, destLatLng];
                if (!routePolyline) {
                    routePolyline = L.polyline(points, {
                        color: '#1a73e8',
                        weight: 4,
                        dashArray: '6, 8',
                        opacity: 0.9,
                        lineCap: 'round'
                    }).addTo(map);
                } else {
                    routePolyline.setLatLngs(points);
                }
            }
        }

        function fitBoth() {
            if (userMarker && destMarker && geofenceCircle) {
                var group = new L.featureGroup([userMarker, destMarker, geofenceCircle]);
                map.fitBounds(group.getBounds().pad(0.3));
            } else if (destMarker) {
                map.setView(destMarker.getLatLng(), 15);
            }
        }

        // Map Control Bridge APIs
        window.centerOnUser = function() {
            if (userMarker) {
                map.flyTo(userMarker.getLatLng(), 16, { duration: 1.2 });
            }
        };

        window.centerOnDest = function() {
            if (destMarker) {
                map.flyTo(destMarker.getLatLng(), 16, { duration: 1.2 });
            }
        };

        window.zoomIn = function() {
            map.zoomIn();
        };

        window.zoomOut = function() {
            map.zoomOut();
        };

        // Tap on map to set destination
        map.on('click', function(e) {
            if (window.Android && window.Android.onMapClicked) {
                window.Android.onMapClicked(e.latlng.lat, e.latlng.lng);
            }
        });

        // Map ready callback
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
