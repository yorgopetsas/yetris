package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScorePanel(
    score: Int,
    best: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Score: $score")
        Text("Best: $best")
    }
}
