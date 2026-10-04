package com.example.service

import com.example.model.LocationPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class SearchService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    // Pre-curated transit stations and hubs for fast instant matching & offline mode
    private val staticTransitHubs = listOf(
        LocationPoint("Paris - Gare de Lyon", "Place Louis-Armand, 75012 Paris", 48.8448, 2.3735),
        LocationPoint("Paris - Gare du Nord", "18 Rue de Dunkerque, 75010 Paris", 48.8809, 2.3553),
        LocationPoint("Paris - Gare Montparnasse", "Place Raoul Dautry, 75015 Paris", 48.8412, 2.3209),
        LocationPoint("Châtelet - Les Halles", "Forum des Halles, Paris", 48.8614, 2.3470),
        LocationPoint("La Défense - Grande Arche", "Puteaux / Courbevoie, Paris", 48.8920, 2.2370),
        LocationPoint("Aéroport Paris-CDG (Terminal 2)", "Tremblay-en-France, Île-de-France", 49.0042, 2.5714),
        LocationPoint("Alger - Gare d'Agha", "Boulevard Mohamed V, Sidi M'Hamed", 36.7628, 3.0583),
        LocationPoint("Alger - Tafourah Grande Poste", "Rue Larbi Ben M'hidi, Alger Centre", 36.7725, 3.0592),
        LocationPoint("Casablanca - Casa-Port", "Boulevard Mohammed V, Casablanca", 33.6001, -7.6163),
        LocationPoint("Tunis - Gare de Tunis Ville", "Place de Barcelone, Tunis", 36.7972, 10.1804),
        LocationPoint("Bruxelles - Gare du Midi", "Avenue Fonsny 47B, 1060 Bruxelles", 50.8357, 4.3356),
        LocationPoint("Lyon - Gare de Lyon-Part-Dieu", "5 Place Charles Béraudier, 69003 Lyon", 45.7606, 4.8596),
        LocationPoint("Marseille - Saint-Charles", "Square Narvik, 13001 Marseille", 43.3032, 5.3806)
    )

    suspend fun searchPlaces(query: String): List<LocationPoint> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return@withContext emptyList()

        // First check static transit stops that match query
        val localMatches = staticTransitHubs.filter {
            it.name.contains(trimmed, ignoreCase = true) || it.address.contains(trimmed, ignoreCase = true)
        }

        // Now attempt live query via OpenStreetMap Nominatim
        val remoteMatches = mutableListOf<LocationPoint>()
        try {
            val encoded = URLEncoder.encode(trimmed, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?q=$encoded&format=json&addressdetails=1&limit=8"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "ARRIVA-Android-TransitAlarm/1.0 (contact: info@arriva.app)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val jsonArray = JSONArray(body)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val displayName = obj.optString("display_name", "")
                            val parts = displayName.split(",", limit = 2)
                            val title = parts.getOrNull(0)?.trim() ?: trimmed
                            val subtitle = parts.getOrNull(1)?.trim() ?: ""
                            val lat = obj.optDouble("lat", 0.0)
                            val lon = obj.optDouble("lon", 0.0)
                            if (lat != 0.0 && lon != 0.0) {
                                remoteMatches.add(LocationPoint(title, subtitle, lat, lon))
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Network fallback to local matches
        }

        val combined = mutableListOf<LocationPoint>()
        combined.addAll(localMatches)
        for (item in remoteMatches) {
            if (combined.none { it.latitude == item.latitude && it.longitude == item.longitude }) {
                combined.add(item)
            }
        }
        combined.take(8)
    }

    fun getSuggestedTransitStops(): List<LocationPoint> {
        return staticTransitHubs.take(6)
    }
}
