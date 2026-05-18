package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yorgo.tetris.domain.InputActionType
import com.yorgo.tetris.domain.SessionStatus

/**
 * Full-width row: Left at start edge, Right at end edge, Rotate + Soft Drop centered between them.
 */
@Composable
fun ControlsPanel(
    status: SessionStatus,
    modifier: Modifier = Modifier,
    onAction: (InputActionType) -> Unit,
    onPauseAndSave: () -> Unit = {}
) {
    val gameplayEnabled = status == SessionStatus.RUNNING
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onAction(InputActionType.MOVE_LEFT) },
                enabled = gameplayEnabled
            ) { Text("Left") }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onAction(InputActionType.ROTATE) },
                    enabled = gameplayEnabled
                ) { Text("Rotate") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onAction(InputActionType.SOFT_DROP) },
                    enabled = gameplayEnabled
                ) { Text("Drop") }
            }
            Button(
                onClick = { onAction(InputActionType.MOVE_RIGHT) },
                enabled = gameplayEnabled
            ) { Text("Right") }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (status) {
                SessionStatus.RUNNING -> {
                    Button(onClick = onPauseAndSave) { Text("Pause") }
                }
                SessionStatus.PAUSED -> {
                    Button(onClick = { onAction(InputActionType.RESUME) }) { Text("Resume") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onPauseAndSave) { Text("Save pause") }
                }
                else -> Unit
            }
        }
    }
}
