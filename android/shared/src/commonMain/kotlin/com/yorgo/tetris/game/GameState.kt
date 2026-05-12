package com.yorgo.tetris.game

import com.yorgo.tetris.domain.GameSession

data class EngineState(
    val session: GameSession = GameSession()
)
