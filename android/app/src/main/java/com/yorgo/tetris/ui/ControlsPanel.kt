package com.yorgo.tetris.ui

import androidx.compose.foundation.layout.Arrangement
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

/**
 * Full-width row: Left at start edge, Right at end edge, Rotate + Soft Drop centered between them.
 */
@Composable
fun ControlsPanel(
    modifier: Modifier = Modifier,
    onAction: (InputActionType) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { onAction(InputActionType.MOVE_LEFT) }) { Text("Left") }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onAction(InputActionType.ROTATE) }) { Text("Rotate") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onAction(InputActionType.SOFT_DROP) }) { Text("Drop") }
        }
        Button(onClick = { onAction(InputActionType.MOVE_RIGHT) }) { Text("Right") }
    }
}
