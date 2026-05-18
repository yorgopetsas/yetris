package com.yorgo.tetris.domain

/** Full in-progress game state for persistent pause/resume. */
data class SavedGameSnapshot(
    val version: Int = CURRENT_VERSION,
    val savedAtEpochMillis: Long,
    val session: GameSession,
    val bagQueue: List<PieceType>
) {
    companion object {
        const val CURRENT_VERSION = 1
    }
}
