package com.yorgo.tetris.data

import android.content.Context
import com.yorgo.tetris.domain.BestScoreStore

class SharedPreferencesBestScoreStore(context: Context) : BestScoreStore {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun readBestScore(): Int = prefs.getInt(KEY_SCORE, 0)

    override fun readBestPlayerName(): String = prefs.getString(KEY_NAME, "").orEmpty()

    override fun writeBest(score: Int, playerName: String) {
        prefs.edit()
            .putInt(KEY_SCORE, score)
            .putString(KEY_NAME, playerName.take(MAX_NAME_LEN))
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "tetris_personal_best"
        private const val KEY_SCORE = "best_score"
        private const val KEY_NAME = "best_player_name"
        const val MAX_NAME_LEN = 24
    }
}
