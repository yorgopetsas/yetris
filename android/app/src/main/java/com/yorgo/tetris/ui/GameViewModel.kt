package com.yorgo.tetris.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yorgo.tetris.domain.BestScoreStore
import com.yorgo.tetris.domain.InMemoryBestScoreStore
import com.yorgo.tetris.domain.InputActionType
import com.yorgo.tetris.domain.SessionStatus
import com.yorgo.tetris.game.GameEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState(
    val status: SessionStatus = SessionStatus.READY,
    val score: Int = 0,
    val bestScore: Int = 0,
    val board: List<List<Boolean>> = emptyList(),
    val activeCells: List<Pair<Int, Int>> = emptyList()
)

class GameViewModel(
    private val engine: GameEngine = GameEngine(),
    private val bestScoreStore: BestScoreStore = InMemoryBestScoreStore()
) : ViewModel() {
    private val _ui = MutableStateFlow(UiState(bestScore = bestScoreStore.readBestScore()))
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        engine.setStateListener { publish() }
        publish()
    }

    fun start() {
        engine.start(viewModelScope)
    }

    fun onAction(action: InputActionType) {
        if (action == InputActionType.START) {
            start()
            return
        }
        engine.dispatch(action)
    }

    private fun publish() {
        val session = engine.session
        if (session.status == SessionStatus.GAME_OVER) {
            bestScoreStore.writeBestScore(session.score)
        }
        val cells = session.activePiece?.let { piece ->
            com.yorgo.tetris.game.CollisionRules.occupiedCells(piece).map { it.x to it.y }
        } ?: emptyList()
        _ui.value = UiState(
            status = session.status,
            score = session.score,
            bestScore = bestScoreStore.readBestScore(),
            board = session.board.cells,
            activeCells = cells
        )
    }

    override fun onCleared() {
        super.onCleared()
        engine.stop()
    }
}
