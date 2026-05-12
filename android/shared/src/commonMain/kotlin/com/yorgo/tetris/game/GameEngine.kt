package com.yorgo.tetris.game

import com.yorgo.tetris.domain.GameSession
import com.yorgo.tetris.domain.InputActionType
import com.yorgo.tetris.domain.Piece
import com.yorgo.tetris.domain.SessionStatus
import kotlinx.coroutines.CoroutineScope

class GameEngine(
    private val pieceGenerator: PieceGenerator = PieceGenerator(),
    private val clock: GameClock = GameClock(GameTuning.TICK_MILLIS)
) {
    var session: GameSession = GameSession()
        private set
    private var stateListener: ((GameSession) -> Unit)? = null

    fun setStateListener(listener: (GameSession) -> Unit) {
        stateListener = listener
    }

    fun start(scope: CoroutineScope) {
        session = RestartLogic.restart(pieceGenerator)
        clock.start(scope) { tick() }
        notifyStateChanged()
    }

    fun stop() = clock.stop()

    fun dispatch(action: InputActionType) {
        when (action) {
            InputActionType.RESTART -> {
                session = RestartLogic.restart(pieceGenerator)
                notifyStateChanged()
                return
            }
            InputActionType.PAUSE -> {
                if (session.status == SessionStatus.RUNNING) {
                    session = session.copy(status = SessionStatus.PAUSED)
                } else {
                    return
                }
            }
            InputActionType.RESUME -> {
                if (session.status == SessionStatus.PAUSED && session.activePiece != null) {
                    session = session.copy(status = SessionStatus.RUNNING)
                } else {
                    return
                }
            }
            InputActionType.START -> return
            else -> {
                val active = session.activePiece ?: return
                when (action) {
                    InputActionType.MOVE_LEFT -> session = session.copy(activePiece = PieceController.move(active, -1, 0, session.board))
                    InputActionType.MOVE_RIGHT -> session = session.copy(activePiece = PieceController.move(active, 1, 0, session.board))
                    InputActionType.ROTATE -> session = session.copy(activePiece = PieceController.rotate(active, session.board))
                    InputActionType.SOFT_DROP -> {
                        tick()
                        return
                    }
                    else -> Unit
                }
            }
        }
        notifyStateChanged()
    }

    private fun tick() {
        if (session.status != SessionStatus.RUNNING) return
        val active = session.activePiece ?: return
        val moved = PieceController.move(active, 0, 1, session.board)
        if (moved == active) {
            lockAndSpawn(active)
        } else {
            session = session.copy(activePiece = moved, tick = session.tick + 1)
            notifyStateChanged()
        }
    }

    private fun lockAndSpawn(piece: Piece) {
        val merged = LockingLogic.merge(session.board, piece)
        val clearResult = LineClearLogic.clear(merged)
        val scoreDelta = ScoreCalculator.pointsFor(clearResult.clearedLines)
        val next = pieceGenerator.next()
        val isGameOver = GameOverRules.isGameOverOnSpawn(next, clearResult.board)
        session = session.copy(
            board = clearResult.board,
            score = session.score + scoreDelta,
            linesCleared = session.linesCleared + clearResult.clearedLines,
            activePiece = if (isGameOver) null else next,
            status = if (isGameOver) SessionStatus.GAME_OVER else SessionStatus.RUNNING,
            tick = session.tick + 1
        )
        notifyStateChanged()
    }

    private fun notifyStateChanged() {
        stateListener?.invoke(session)
    }
}
