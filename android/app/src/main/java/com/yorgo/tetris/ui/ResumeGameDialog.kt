package com.yorgo.tetris.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.text.DateFormat
import java.util.Date

@Composable
fun ResumeGameDialog(
    score: Int,
    savedAtEpochMillis: Long,
    onContinue: () -> Unit,
    onNewGame: () -> Unit
) {
    val whenSaved = if (savedAtEpochMillis > 0L) {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
            .format(Date(savedAtEpochMillis))
    } else {
        "earlier"
    }
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Saved game found") },
        text = {
            Text("Paused run: score $score (saved $whenSaved). Continue or start a new game?")
        },
        confirmButton = {
            TextButton(onClick = onContinue) { Text("Continue") }
        },
        dismissButton = {
            TextButton(onClick = onNewGame) { Text("New game") }
        }
    )
}
