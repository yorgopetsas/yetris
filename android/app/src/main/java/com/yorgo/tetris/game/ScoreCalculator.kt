package com.yorgo.tetris.game

object ScoreCalculator {
    fun pointsFor(clearedLines: Int): Int = when (clearedLines) {
        1 -> 100
        2 -> 300
        3 -> 500
        4 -> 800
        else -> 0
    }
}
