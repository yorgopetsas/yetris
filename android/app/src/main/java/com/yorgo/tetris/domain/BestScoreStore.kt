package com.yorgo.tetris.domain

/** Persists personal best score and optional display name (device-local). */
interface BestScoreStore {
    fun readBestScore(): Int
    fun readBestPlayerName(): String
    fun writeBest(score: Int, playerName: String)
}
