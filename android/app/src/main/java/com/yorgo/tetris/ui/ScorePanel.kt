package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yorgo.tetris.domain.SessionStatus

@Composable
fun ScorePanel(
    score: Int,
    best: Int,
    bestPlayerName: String,
    status: SessionStatus = SessionStatus.READY,
    onOpenLeaderboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Score: $score")
        if (status == SessionStatus.PAUSED) {
            Text("Status: Paused")
        }
        val bestLine = if (bestPlayerName.isNotBlank()) {
            "Best on this device: $bestPlayerName — $best"
        } else {
            "Best on this device: $best"
        }
        Text(bestLine)
        TextButton(onClick = onOpenLeaderboard) {
            Text("Top 10 high scores (all players)")
        }
    }
}
