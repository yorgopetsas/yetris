package com.yorgo.tetris.game

import com.yorgo.tetris.domain.GameSession
import com.yorgo.tetris.domain.SessionStatus

object RestartLogic {
    fun restart(generator: PieceGenerator): GameSession {
        generator.resetSession()
        return GameSession(
            status = SessionStatus.RUNNING,
            activePiece = generator.next()
        )
    }
}
