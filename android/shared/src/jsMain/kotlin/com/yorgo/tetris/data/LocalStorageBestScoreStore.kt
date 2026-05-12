package com.yorgo.tetris.data

import com.yorgo.tetris.domain.BestScoreStore
import kotlinx.browser.window

class LocalStorageBestScoreStore : BestScoreStore {

    override fun readBestScore(): Int =
        window.localStorage.getItem(KEY_SCORE)?.toIntOrNull() ?: 0

    override fun readBestPlayerName(): String =
        window.localStorage.getItem(KEY_NAME).orEmpty()

    override fun writeBest(score: Int, playerName: String) {
        window.localStorage.setItem(KEY_SCORE, score.toString())
        window.localStorage.setItem(KEY_NAME, playerName.take(MAX_NAME_LEN))
    }

    companion object {
        private const val KEY_SCORE = "best_score"
        private const val KEY_NAME = "best_player_name"
        const val MAX_NAME_LEN = 24
    }
}
