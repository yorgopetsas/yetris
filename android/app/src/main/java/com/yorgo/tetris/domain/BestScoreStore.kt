package com.yorgo.tetris.domain

interface BestScoreStore {
    fun readBestScore(): Int
    fun writeBestScore(score: Int)
}

class InMemoryBestScoreStore : BestScoreStore {
    private var best = 0

    override fun readBestScore(): Int = best

    override fun writeBestScore(score: Int) {
        if (score > best) best = score
    }
}
