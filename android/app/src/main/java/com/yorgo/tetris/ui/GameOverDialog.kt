package com.yorgo.tetris.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GameOverDialog(
    score: Int,
    onRestart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Game Over") },
        text = { Text("Final score: $score") },
        confirmButton = {
            Button(onClick = onRestart) { Text("Restart") }
        }
    )
}
