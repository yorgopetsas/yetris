package com.yorgo.tetris.leaderboard

import android.content.Context
import com.yorgo.tetris.BuildConfig

class FriendsLeaderboardRepository(context: Context) {
    private val client = FriendsLeaderboardClient(
        baseUrl = BuildConfig.LEADERBOARD_URL.trim(),
        token = BuildConfig.LEADERBOARD_TOKEN.trim()
    )
    private val cache = LeaderboardCache(context)

    fun cachedEntries(): List<LeaderboardEntry> = cache.read()

    suspend fun refreshTop(limit: Int = 10): List<LeaderboardEntry> {
        if (!client.isConfigured) return cache.read()
        val result = client.fetchTop(limit)
        val entries = result.getOrElse { cache.read() }
        if (result.isSuccess) cache.write(entries)
        return entries
    }

    suspend fun submitScore(name: String, score: Int) {
        if (!client.isConfigured) return
        client.submit(name, score)
        refreshTop()
    }
}
