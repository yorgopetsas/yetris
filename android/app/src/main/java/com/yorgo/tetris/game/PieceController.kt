package com.yorgo.tetris.game

import com.yorgo.tetris.domain.GameBoard
import com.yorgo.tetris.domain.Piece
import com.yorgo.tetris.domain.Point

object PieceController {
    fun move(piece: Piece, dx: Int, dy: Int, board: GameBoard): Piece {
        val candidate = piece.copy(origin = Point(piece.origin.x + dx, piece.origin.y + dy))
        return if (CollisionRules.isValidPlacement(candidate, board)) candidate else piece
    }

    fun rotate(piece: Piece, board: GameBoard): Piece {
        val candidate = piece.copy(rotation = piece.rotation + 1)
        return if (CollisionRules.isValidPlacement(candidate, board)) candidate else piece
    }
}
