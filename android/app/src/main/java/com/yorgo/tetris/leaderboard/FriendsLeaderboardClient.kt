package com.yorgo.tetris.leaderboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class FriendsLeaderboardClient(
    private val baseUrl: String,
    private val token: String
) {
    val isConfigured: Boolean
        get() = baseUrl.isNotBlank() && token.isNotBlank()

    suspend fun fetchTop(limit: Int = 10): Result<List<LeaderboardEntry>> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext Result.success(emptyList())
        runCatching {
            val url = URL(
                "$baseUrl?token=${encode(token)}&limit=$limit"
            )
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            try {
                val code = conn.responseCode
                val body = (if (code in 200..299) conn.inputStream else conn.errorStream)
                    ?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (code !in 200..299) error("HTTP $code: $body")
                parseEntries(JSONArray(body))
            } finally {
                conn.disconnect()
            }
        }
    }

    suspend fun submit(name: String, score: Int): Result<Unit> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext Result.success(Unit)
        runCatching {
            val url = URL(
                "$baseUrl?token=${encode(token)}&action=submit" +
                    "&name=${encode(name)}&score=$score"
            )
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            try {
                val code = conn.responseCode
                val body = (if (code in 200..299) conn.inputStream else conn.errorStream)
                    ?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (code !in 200..299) error("HTTP $code: $body")
                if (!body.contains("\"ok\"")) error("Unexpected response: $body")
            } finally {
                conn.disconnect()
            }
        }
    }

    private fun parseEntries(array: JSONArray): List<LeaderboardEntry> {
        val out = mutableListOf<LeaderboardEntry>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            out.add(
                LeaderboardEntry(
                    name = obj.getString("name"),
                    score = obj.getInt("score")
                )
            )
        }
        return out
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, Charsets.UTF_8.name())
}
