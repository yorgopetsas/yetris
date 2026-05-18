package com.yorgo.tetris.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yorgo.tetris.domain.InputActionType
import com.yorgo.tetris.domain.SessionStatus

@Composable
fun GameScreen(vm: GameViewModel = viewModel()) {
    val state by vm.ui.collectAsState()
    LaunchedEffect(state.status, state.showResumePrompt) {
        if (!state.showResumePrompt && state.status == SessionStatus.READY) {
            vm.onAction(InputActionType.START)
        }
    }

    if (state.showResumePrompt) {
        ResumeGameDialog(
            score = state.savedGameScore,
            savedAtEpochMillis = state.savedGameAtEpochMillis,
            onContinue = vm::onContinueSavedGame,
            onNewGame = vm::onDiscardSavedGameAndStartNew
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScorePanel(
            score = state.score,
            best = state.bestScore,
            bestPlayerName = state.bestPlayerName,
            status = state.status
        )
        FriendsLeaderboardPanel(
            entries = state.friendsLeaderboard,
            configured = state.leaderboardConfigured
        )
        if (state.activeCells.isEmpty() && state.status == SessionStatus.READY && !state.showResumePrompt) {
            Button(onClick = { vm.onAction(InputActionType.START) }) {
                Text("Start Game")
            }
        }
        if (state.status == SessionStatus.PAUSED) {
            Text("Paused", style = MaterialTheme.typography.titleMedium)
        }
        BoardGrid(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        ControlsPanel(
            status = state.status,
            onAction = vm::onAction,
            onPauseAndSave = vm::onPauseAndSave
        )
        if (state.status == SessionStatus.GAME_OVER) {
            GameOverDialog(
                score = state.score,
                needsNameEntry = state.gameOverNeedsNameEntry,
                onSaveHighScoreAndRestart = { vm.onSaveHighScoreAndRestart(it) },
                onRestart = { vm.onAction(InputActionType.RESTART) }
            )
        }
    }
}

@Composable
private fun BoardGrid(
    state: UiState,
    modifier: Modifier = Modifier
) {
    val rows = if (state.board.isEmpty()) List(20) { List(10) { false } } else state.board
    val activeSet = state.activeCells.toSet()
    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {
        val cellSize = minOf(maxWidth / 10, maxHeight / 20).coerceAtLeast(1.dp)
        val boardWidth = cellSize * 10
        val boardHeight = cellSize * 20

        Box(
            modifier = Modifier
                .size(boardWidth, boardHeight)
                .border(2.dp, Color.Black)
                .background(Color.Black.copy(alpha = 0.08f))
                .align(Alignment.Center)
        ) {
            Column {
                rows.forEachIndexed { y, row ->
                    Row {
                        row.forEachIndexed { x, cell ->
                            val active = activeSet.contains(x to y)
                            Spacer(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(0.5.dp, Color.Black.copy(alpha = 0.2f))
                                    .background(
                                        when {
                                            active -> Color(0xFF4CAF50)
                                            cell -> Color(0xFF455A64)
                                            else -> Color(0xFFECEFF1)
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
