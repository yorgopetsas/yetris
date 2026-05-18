package com.yorgo.tetris.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yorgo.tetris.data.SavedGameStore
import com.yorgo.tetris.data.SharedPreferencesBestScoreStore
import com.yorgo.tetris.data.SharedPreferencesSavedGameStore
import com.yorgo.tetris.domain.BestScoreStore
import com.yorgo.tetris.domain.InputActionType
import com.yorgo.tetris.domain.SessionStatus
import com.yorgo.tetris.game.GameEngine
import com.yorgo.tetris.leaderboard.FriendsLeaderboardRepository
import com.yorgo.tetris.leaderboard.LeaderboardEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val status: SessionStatus = SessionStatus.READY,
    val score: Int = 0,
    val bestScore: Int = 0,
    val bestPlayerName: String = "",
    val board: List<List<Boolean>> = emptyList(),
    val activeCells: List<Pair<Int, Int>> = emptyList(),
    val gameOverNeedsNameEntry: Boolean = false,
    val showResumePrompt: Boolean = false,
    val savedGameScore: Int = 0,
    val savedGameAtEpochMillis: Long = 0L,
    val friendsLeaderboard: List<LeaderboardEntry> = emptyList(),
    val leaderboardConfigured: Boolean = false
)

class GameViewModel @JvmOverloads constructor(
    application: Application,
    private val engine: GameEngine = GameEngine(),
    private val bestScoreStore: BestScoreStore = SharedPreferencesBestScoreStore(application),
    private val savedGameStore: SavedGameStore = SharedPreferencesSavedGameStore(application),
    private val leaderboardRepository: FriendsLeaderboardRepository = FriendsLeaderboardRepository(application)
) : AndroidViewModel(application) {

    private val _ui = MutableStateFlow(
        UiState(
            bestScore = bestScoreStore.readBestScore(),
            bestPlayerName = bestScoreStore.readBestPlayerName(),
            showResumePrompt = savedGameStore.hasSavedGame(),
            savedGameScore = savedGameStore.load()?.session?.score ?: 0,
            savedGameAtEpochMillis = savedGameStore.load()?.savedAtEpochMillis ?: 0L,
            friendsLeaderboard = leaderboardRepository.cachedEntries(),
            leaderboardConfigured = com.yorgo.tetris.BuildConfig.LEADERBOARD_URL.isNotBlank()
        )
    )
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        engine.setStateListener { publish() }
        publish()
        refreshLeaderboard()
    }

    fun start() {
        engine.start(viewModelScope)
    }

    fun onAction(action: InputActionType) {
        when (action) {
            InputActionType.START -> {
                savedGameStore.clear()
                start()
                return
            }
            InputActionType.RESTART -> savedGameStore.clear()
            else -> Unit
        }
        if (action == InputActionType.RESUME) {
            engine.dispatch(InputActionType.RESUME)
            engine.ensureClockStarted(viewModelScope)
            publish()
            return
        }
        engine.dispatch(action)
        publish()
    }

    fun onPauseAndSave() {
        val status = engine.session.status
        if (status != SessionStatus.RUNNING && status != SessionStatus.PAUSED) return
        if (status == SessionStatus.RUNNING) {
            engine.dispatch(InputActionType.PAUSE)
        }
        val snapshot = engine.buildSnapshot(System.currentTimeMillis())
        savedGameStore.save(snapshot)
        publish()
    }

    fun onContinueSavedGame() {
        val snapshot = savedGameStore.load() ?: return
        engine.restore(snapshot)
        _ui.value = _ui.value.copy(showResumePrompt = false)
        publish()
    }

    fun onDiscardSavedGameAndStartNew() {
        savedGameStore.clear()
        _ui.value = _ui.value.copy(showResumePrompt = false)
        start()
    }

    fun onSaveHighScoreAndRestart(name: String) {
        val session = engine.session
        val trimmed = name.trim().take(SharedPreferencesBestScoreStore.MAX_NAME_LEN)
            .ifEmpty { "Anonymous" }
        val beatBest = session.status == SessionStatus.GAME_OVER &&
            session.score > bestScoreStore.readBestScore()
        if (beatBest) {
            bestScoreStore.writeBest(session.score, trimmed)
            viewModelScope.launch {
                leaderboardRepository.submitScore(trimmed, session.score)
            }
        }
        savedGameStore.clear()
        engine.dispatch(InputActionType.RESTART)
        publish()
        refreshLeaderboard()
    }

    private fun refreshLeaderboard() {
        viewModelScope.launch {
            val entries = leaderboardRepository.refreshTop(limit = 10)
            _ui.value = _ui.value.copy(
                friendsLeaderboard = entries,
                leaderboardConfigured = com.yorgo.tetris.BuildConfig.LEADERBOARD_URL.isNotBlank()
            )
        }
    }

    private fun publish() {
        val session = engine.session
        val prevBest = bestScoreStore.readBestScore()
        val needsName =
            session.status == SessionStatus.GAME_OVER && session.score > prevBest
        val cells = session.activePiece?.let { piece ->
            com.yorgo.tetris.game.CollisionRules.occupiedCells(piece).map { it.x to it.y }
        } ?: emptyList()
        val saved = savedGameStore.load()
        _ui.value = UiState(
            status = session.status,
            score = session.score,
            bestScore = bestScoreStore.readBestScore(),
            bestPlayerName = bestScoreStore.readBestPlayerName(),
            board = session.board.cells,
            activeCells = cells,
            gameOverNeedsNameEntry = needsName,
            showResumePrompt = _ui.value.showResumePrompt,
            savedGameScore = saved?.session?.score ?: _ui.value.savedGameScore,
            savedGameAtEpochMillis = saved?.savedAtEpochMillis ?: _ui.value.savedGameAtEpochMillis,
            friendsLeaderboard = _ui.value.friendsLeaderboard,
            leaderboardConfigured = com.yorgo.tetris.BuildConfig.LEADERBOARD_URL.isNotBlank()
        )
    }

    override fun onCleared() {
        super.onCleared()
        engine.stop()
    }
}
