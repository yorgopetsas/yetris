package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScorePanel(
    score: Int,
    best: Int,
    bestPlayerName: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Score: $score")
        val bestLine = if (bestPlayerName.isNotBlank()) {
            "Best: $bestPlayerName — $best"
        } else {
            "Best: $best"
        }
        Text(bestLine)
    }
}
