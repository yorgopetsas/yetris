package com.yorgo.tetris.domain

enum class SessionStatus {
    READY,
    RUNNING,
    PAUSED,
    GAME_OVER
}

enum class InputActionType {
    START,
    MOVE_LEFT,
    MOVE_RIGHT,
    ROTATE,
    SOFT_DROP,
    PAUSE,
    RESUME,
    RESTART
}

object BoardConfig {
    const val WIDTH = 10
    const val HEIGHT = 20
}
