package com.yorgo.tetris.game

import com.yorgo.tetris.domain.GameBoard
import com.yorgo.tetris.domain.Piece

object LockingLogic {
    fun merge(board: GameBoard, piece: Piece): GameBoard {
        val mutable = board.cells.map { it.toMutableList() }.toMutableList()
        CollisionRules.occupiedCells(piece).forEach { point ->
            if (point.y in mutable.indices && point.x in mutable[0].indices) {
                mutable[point.y][point.x] = true
            }
        }
        return board.copy(cells = mutable.map { it.toList() })
    }
}
