package org.sableos.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.telephony.TelephonyManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

/** What an FM station is, from the public radio directory: its name, genre and an internet stream of the same broadcast. */
internal data class FmDirectoryEntry(
    val station: Int,
    val name: String,
    val genre: String = "",
    val streamUrl: String = "",
)

/** The online side of the FM screen; Compose state like [FmViewState] so the screens read it without new parameters. */
internal object FmOnline {
    /** Stations of the person's country, by frequency (100 kHz units). */
    var directory by mutableStateOf(emptyMap<Int, FmDirectoryEntry>())

    /** "Monterrey" when the phone can name the place, else the country; empty when unknown. */
    var region by mutableStateOf("")

    /** What the station's internet stream says is playing ("Artist - Title"); empty when it says nothing. */
    var song by mutableStateOf("")
}

internal object FmDirectoryParser {
    private val frequency = Regex("""(?<![\d.,])(8[7-9]|9\d|10[0-8])[.,](\d)(?!\d)""")

    /** The frequency in 100 kHz units named in a station's [name] ("Classic 106.9 FM" is 1069), or null. */
    fun frequencyOf(name: String): Int? {
        val match = frequency.find(name) ?: return null
        val station = match.groupValues[1].toInt() * 10 + match.groupValues[2].toInt()
        return station.takeIf(FmBand::isValid)
    }

    /**
     * Radio Browser's JSON array, keeping stations whose name says their FM frequency. The most listened
     * station wins when two claim one frequency, so the list is read in the order the service sends it.
     */
    fun parse(json: String): Map<Int, FmDirectoryEntry> {
        val array = runCatching { JSONArray(json) }.getOrNull() ?: return emptyMap()
        val out = LinkedHashMap<Int, FmDirectoryEntry>()
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val name = item.optString("name").trim()
            val station = frequencyOf(name) ?: continue
            if (station in out) continue
            val stream = item.optString("url_resolved").ifBlank { item.optString("url") }
            out[station] =
                FmDirectoryEntry(
                    station = station,
                    name = name,
                    genre =
                        item
                            .optString("tags")
                            .split(',')
                            .map { it.trim() }
                            .firstOrNull { it.isNotEmpty() }
                            ?.replaceFirstChar { it.uppercase() }
                            .orEmpty(),
                    streamUrl = stream.takeIf { it.startsWith("https://") }.orEmpty(),
                )
        }
        return out
    }

    fun encode(entries: Map<Int, FmDirectoryEntry>): String =
        JSONArray()
            .also { array ->
                entries.values.forEach {
                    array.put(
                        JSONObject()
                            .put("s", it.station)
                            .put("n", it.name)
                            .put("g", it.genre)
                            .put("u", it.streamUrl),
                    )
                }
            }.toString()

    fun decode(json: String): Map<Int, FmDirectoryEntry> {
        val array = runCatching { JSONArray(json) }.getOrNull() ?: return emptyMap()
        val out = LinkedHashMap<Int, FmDirectoryEntry>()
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val station = item.optInt("s")
            if (FmBand.isValid(station)) {
                out[station] = FmDirectoryEntry(station, item.optString("n"), item.optString("g"), item.optString("u"))
            }
        }
        return out
    }
}

/** Reads "StreamTitle" from an Icecast/Shoutcast stream, the song a radio station's internet stream is playing. */
internal object IcyMetadata {
    /** "Artist - Title" from a metadata block like `StreamTitle='Artist - Title';StreamUrl='';`, or null when empty. */
    fun streamTitle(block: String): String? {
        val match = Regex("StreamTitle='(.*?)';", RegexOption.DOT_MATCHES_ALL).find(block) ?: return null
        return match.groupValues[1].trim().takeIf { it.isNotEmpty() }
    }

    /** Splits "Artist - Title" into (artist, title); the title alone has no artist. */
    fun split(text: String): Pair<String, String> {
        val at = text.indexOf(" - ")
        return if (at > 0) text.substring(0, at).trim() to text.substring(at + 3).trim() else "" to text
    }

    /** Reads the stream far enough for one metadata block. Blocking; call off the main thread. */
    fun read(
        stream: InputStream,
        metaInterval: Int,
    ): String? {
        if (metaInterval <= 0) return null
        var left = metaInterval.toLong()
        while (left > 0) {
            val skipped = stream.skip(left)
            if (skipped <= 0) {
                if (stream.read() < 0) return null
                left--
            } else {
                left -= skipped
            }
        }
        val length = stream.read() * 16
        if (length <= 0) return null
        val buffer = ByteArrayOutputStream()
        var remaining = length
        val chunk = ByteArray(length)
        while (remaining > 0) {
            val got = stream.read(chunk, 0, remaining)
            if (got < 0) return null
            buffer.write(chunk, 0, got)
            remaining -= got
        }
        return streamTitle(buffer.toString(Charsets.UTF_8.name()))
    }

    fun fetch(url: String): String? {
        if (!url.startsWith("https://")) return null
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.setRequestProperty("Icy-MetaData", "1")
            connection.setRequestProperty("User-Agent", "SableMedia")
            read(connection.inputStream, connection.getHeaderField("icy-metaint")?.toIntOrNull() ?: 0)
        } catch (_: java.io.IOException) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private const val TIMEOUT_MS = 8_000
}

/** Where the phone is, for the Stations header and the station directory. Needs only coarse location. */
internal object FmRegion {
    fun country(context: Context): String {
        val telephony = context.getSystemService(TelephonyManager::class.java)
        val code =
            telephony?.networkCountryIso.orEmpty().ifBlank { telephony?.simCountryIso.orEmpty() }
                .ifBlank { Locale.getDefault().country }
        return code.uppercase(Locale.ROOT)
    }

    /** The town the phone is in when location is allowed and a geocoder exists, else the country's name. */
    @Suppress("DEPRECATION", "MissingPermission")
    fun place(context: Context): String {
        val country = country(context)
        val fallback = if (country.isEmpty()) "" else Locale.Builder().setRegion(country).build().displayCountry
        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return fallback
        return runCatching {
            val manager = context.getSystemService(LocationManager::class.java)
            val here =
                manager.getProviders(true).mapNotNull { manager.getLastKnownLocation(it) }.maxByOrNull { it.time }
                    ?: return fallback
            Geocoder(context, Locale.getDefault())
                .getFromLocation(here.latitude, here.longitude, 1)
                ?.firstOrNull()
                ?.let { it.locality ?: it.subAdminArea }
                ?: fallback
        }.getOrDefault(fallback)
    }
}

/** The public Radio Browser directory, cached for a week per country. Blocking; call off the main thread. */
internal class FmDirectoryRepository(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("sable_media_fm_directory", Context.MODE_PRIVATE)

    fun cached(country: String): Map<Int, FmDirectoryEntry> =
        preferences.getString("entries_$country", null)?.let(FmDirectoryParser::decode).orEmpty()

    fun load(country: String): Map<Int, FmDirectoryEntry> {
        val fresh = System.currentTimeMillis() - preferences.getLong("at_$country", 0L) < WEEK_MS
        if (fresh) cached(country).takeIf { it.isNotEmpty() }?.let { return it }
        val fetched = runCatching { fetch(country) }.getOrNull()
        if (!fetched.isNullOrEmpty()) {
            preferences
                .edit()
                .putString("entries_$country", FmDirectoryParser.encode(fetched))
                .putLong("at_$country", System.currentTimeMillis())
                .apply()
            return fetched
        }
        return cached(country)
    }

    private fun fetch(country: String): Map<Int, FmDirectoryEntry> {
        val url =
            "https://all.api.radio-browser.info/json/stations/search?countrycode=$country" +
                "&hidebroken=true&order=clickcount&reverse=true&limit=1000"
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            connection.setRequestProperty("User-Agent", "SableMedia")
            FmDirectoryParser.parse(connection.inputStream.bufferedReader().use { it.readText() })
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val WEEK_MS = 7L * 24 * 60 * 60 * 1000
    }
}
