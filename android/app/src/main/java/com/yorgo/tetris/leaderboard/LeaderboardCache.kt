package com.yorgo.tetris.leaderboard

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardCache(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun read(): List<LeaderboardEntry> {
        val raw = prefs.getString(KEY_JSON, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(LeaderboardEntry(o.getString("name"), o.getInt("score")))
                }
            }
        }.getOrDefault(emptyList())
    }

    fun write(entries: List<LeaderboardEntry>) {
        val arr = JSONArray()
        entries.forEach { e ->
            arr.put(JSONObject().put("name", e.name).put("score", e.score))
        }
        prefs.edit().putString(KEY_JSON, arr.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "tetris_friends_leaderboard"
        private const val KEY_JSON = "cached_entries"
    }
}
