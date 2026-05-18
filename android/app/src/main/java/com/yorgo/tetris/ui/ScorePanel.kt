package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yorgo.tetris.domain.SessionStatus

@Composable
fun ScorePanel(
    score: Int,
    best: Int,
    bestPlayerName: String,
    status: SessionStatus = SessionStatus.READY,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Score: $score")
        if (status == SessionStatus.PAUSED) {
            Text("Status: Paused")
        }
        val bestLine = if (bestPlayerName.isNotBlank()) {
            "Best: $bestPlayerName — $best"
        } else {
            "Best: $best"
        }
        Text(bestLine)
    }
}
