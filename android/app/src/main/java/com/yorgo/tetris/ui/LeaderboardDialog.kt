package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yorgo.tetris.leaderboard.LeaderboardEntry

@Composable
fun LeaderboardDialog(
    entries: List<LeaderboardEntry>,
    configured: Boolean,
    loading: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Top 10 high scores") },
        text = {
            when {
                !configured -> Text(
                    "Global scores are not configured on this build. " +
                        "Add LEADERBOARD_URL and LEADERBOARD_TOKEN to android/local.properties, " +
                        "rebuild the APK, and use the same Google Sheet for all devices and the web demo."
                )
                loading -> CircularProgressIndicator()
                entries.isEmpty() -> Text("No scores yet. Beat your personal best and enter your name to post a score.")
                else -> Column(Modifier.verticalScroll(rememberScrollState())) {
                    entries.take(10).forEachIndexed { index, entry ->
                        Text("${index + 1}. ${entry.name} — ${entry.score}")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
