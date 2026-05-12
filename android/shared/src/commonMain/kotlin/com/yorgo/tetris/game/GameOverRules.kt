package com.yorgo.tetris.game

import com.yorgo.tetris.domain.GameBoard
import com.yorgo.tetris.domain.Piece

object GameOverRules {
    fun isGameOverOnSpawn(piece: Piece, board: GameBoard): Boolean {
        return !CollisionRules.isValidPlacement(piece, board)
    }
}
