package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yorgo.tetris.leaderboard.LeaderboardEntry

@Composable
fun FriendsLeaderboardPanel(
    entries: List<LeaderboardEntry>,
    configured: Boolean,
    modifier: Modifier = Modifier
) {
    if (!configured && entries.isEmpty()) return
    Column(modifier = modifier) {
        Text("Friends top 5", style = MaterialTheme.typography.labelLarge)
        if (entries.isEmpty()) {
            Text("Leaderboard unavailable", style = MaterialTheme.typography.bodySmall)
        } else {
            entries.take(5).forEachIndexed { index, entry ->
                Text("${index + 1}. ${entry.name} — ${entry.score}")
            }
        }
    }
}
