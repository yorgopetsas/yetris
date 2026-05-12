package com.yorgo.tetris.game

import com.yorgo.tetris.domain.GameBoard
import com.yorgo.tetris.domain.Piece
import com.yorgo.tetris.domain.PieceLibrary
import com.yorgo.tetris.domain.Point

object CollisionRules {
    fun isValidPlacement(piece: Piece, board: GameBoard): Boolean {
        return occupiedCells(piece).all { p ->
            p.x in 0 until board.width &&
                p.y in 0 until board.height &&
                !board.cells[p.y][p.x]
        }
    }

    fun occupiedCells(piece: Piece): List<Point> {
        val rotations = PieceLibrary.shapes.getValue(piece.type)
        val shape = rotations[piece.rotation.mod(rotations.size)]
        return shape.map { Point(piece.origin.x + it.x, piece.origin.y + it.y) }
    }
}
