package com.yorgo.tetris.game

import com.yorgo.tetris.domain.Piece
import com.yorgo.tetris.domain.PieceType
import com.yorgo.tetris.domain.Point
import kotlin.random.Random

/**
 * Guideline-style 7-bag: each bag holds one of each tetromino, shuffled with per-session entropy.
 */
class PieceGenerator {
    private var random: Random = Random.Default
    private val bag = mutableListOf<PieceType>()

    /** Call when starting a new game session or full restart so bags and RNG differ per run. */
    fun resetSession() {
        val seed = entropySeed()
        random = Random(seed)
        bag.clear()
        refillBag()
    }

    private fun refillBag() {
        bag.clear()
        bag.addAll(PieceType.entries)
        bag.shuffle(random)
    }

    fun next(): Piece {
        if (bag.isEmpty()) refillBag()
        val type = bag.removeAt(0)
        return Piece(type = type, origin = Point(5, 1))
    }

    /** Remaining spawn order in the current (and partial next) bag. */
    fun snapshotQueue(): List<PieceType> = bag.toList()

    fun restoreQueue(queue: List<PieceType>) {
        bag.clear()
        bag.addAll(queue)
    }
}
