package com.yorgo.tetris.game

import com.yorgo.tetris.domain.GameBoard

data class LineClearResult(
    val board: GameBoard,
    val clearedLines: Int
)

object LineClearLogic {
    fun clear(board: GameBoard): LineClearResult {
        val remainingRows = board.cells.filterNot { row -> row.all { it } }
        val cleared = board.height - remainingRows.size
        val filledTop = List(cleared) { List(board.width) { false } }
        return LineClearResult(
            board = board.copy(cells = (filledTop + remainingRows).takeLast(board.height)),
            clearedLines = cleared
        )
    }
}
