package com.yorgo.tetris.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yorgo.tetris.data.SharedPreferencesBestScoreStore
import com.yorgo.tetris.domain.BestScoreStore
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
    val bestPlayerName: String = "",
    val board: List<List<Boolean>> = emptyList(),
    val activeCells: List<Pair<Int, Int>> = emptyList(),
    /** True when game over and run beats stored best — show name entry before saving. */
    val gameOverNeedsNameEntry: Boolean = false
)

class GameViewModel @JvmOverloads constructor(
    application: Application,
    private val engine: GameEngine = GameEngine(),
    private val bestScoreStore: BestScoreStore = SharedPreferencesBestScoreStore(application)
) : AndroidViewModel(application) {

    private val _ui = MutableStateFlow(
        UiState(
            bestScore = bestScoreStore.readBestScore(),
            bestPlayerName = bestScoreStore.readBestPlayerName()
        )
    )
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

    /** Called when the player confirms name (or empty → Anonymous) after a new personal best. */
    fun onSaveHighScoreAndRestart(name: String) {
        val session = engine.session
        val trimmed = name.trim().take(SharedPreferencesBestScoreStore.MAX_NAME_LEN)
            .ifEmpty { "Anonymous" }
        if (session.status == SessionStatus.GAME_OVER && session.score > bestScoreStore.readBestScore()) {
            bestScoreStore.writeBest(session.score, trimmed)
        }
        engine.dispatch(InputActionType.RESTART)
        publish()
    }

    private fun publish() {
        val session = engine.session
        val prevBest = bestScoreStore.readBestScore()
        val needsName =
            session.status == SessionStatus.GAME_OVER && session.score > prevBest
        val cells = session.activePiece?.let { piece ->
            com.yorgo.tetris.game.CollisionRules.occupiedCells(piece).map { it.x to it.y }
        } ?: emptyList()
        _ui.value = UiState(
            status = session.status,
            score = session.score,
            bestScore = bestScoreStore.readBestScore(),
            bestPlayerName = bestScoreStore.readBestPlayerName(),
            board = session.board.cells,
            activeCells = cells,
            gameOverNeedsNameEntry = needsName
        )
    }

    override fun onCleared() {
        super.onCleared()
        engine.stop()
    }
}
