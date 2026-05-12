package com.yorgo.tetris.game

import com.yorgo.tetris.domain.Piece
import com.yorgo.tetris.domain.PieceType
import com.yorgo.tetris.domain.Point
import kotlin.random.Random

class PieceGenerator(seed: Int = 42) {
    private val random = Random(seed)

    fun next(): Piece {
        val type = PieceType.entries[random.nextInt(PieceType.entries.size)]
        return Piece(type = type, origin = Point(5, 1))
    }
}
