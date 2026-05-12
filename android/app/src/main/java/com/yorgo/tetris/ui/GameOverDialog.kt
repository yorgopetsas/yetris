package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yorgo.tetris.data.SharedPreferencesBestScoreStore

@Composable
fun GameOverDialog(
    score: Int,
    needsNameEntry: Boolean,
    onSaveHighScoreAndRestart: (String) -> Unit,
    onRestart: () -> Unit
) {
    if (needsNameEntry) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {},
            title = { Text("New personal best!") },
            text = {
                Column {
                    Text("Score: $score")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= SharedPreferencesBestScoreStore.MAX_NAME_LEN) name = it },
                        label = { Text("Your name") },
                        singleLine = true,
                        placeholder = { Text("Leave empty for Anonymous") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = { onSaveHighScoreAndRestart(name) }) {
                    Text("Save & restart")
                }
            },
            dismissButton = {
                TextButton(onClick = { onSaveHighScoreAndRestart("") }) {
                    Text("Anonymous & restart")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Game over") },
            text = { Text("Final score: $score") },
            confirmButton = {
                Button(onClick = onRestart) { Text("Restart") }
            }
        )
    }
}
