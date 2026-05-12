package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yorgo.tetris.domain.InputActionType

@Composable
fun ControlsPanel(
    modifier: Modifier = Modifier,
    onAction: (InputActionType) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onAction(InputActionType.MOVE_LEFT) }) { Text("Left") }
            Button(onClick = { onAction(InputActionType.SOFT_DROP) }) { Text("Drop") }
            Button(onClick = { onAction(InputActionType.MOVE_RIGHT) }) { Text("Right") }
        }
        Button(onClick = { onAction(InputActionType.ROTATE) }) { Text("Rotate") }
    }
}
