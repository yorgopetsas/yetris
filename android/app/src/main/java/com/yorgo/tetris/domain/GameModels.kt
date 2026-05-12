package com.yorgo.tetris.domain

data class Point(val x: Int, val y: Int)

enum class PieceType {
    I, O, T, L, J, S, Z
}

data class Piece(
    val type: PieceType,
    val origin: Point,
    val rotation: Int = 0
)

data class InputAction(
    val type: InputActionType,
    val issuedAtTick: Long
)

data class GameBoard(
    val width: Int = BoardConfig.WIDTH,
    val height: Int = BoardConfig.HEIGHT,
    val cells: List<List<Boolean>> = List(BoardConfig.HEIGHT) { List(BoardConfig.WIDTH) { false } }
)

data class GameSession(
    val status: SessionStatus = SessionStatus.READY,
    val score: Int = 0,
    val linesCleared: Int = 0,
    val level: Int = 1,
    val tick: Long = 0,
    val board: GameBoard = GameBoard(),
    val activePiece: Piece? = null
)

object PieceLibrary {
    val shapes: Map<PieceType, List<List<Point>>> = mapOf(
        PieceType.I to listOf(
            listOf(Point(-1, 0), Point(0, 0), Point(1, 0), Point(2, 0)),
            listOf(Point(0, -1), Point(0, 0), Point(0, 1), Point(0, 2))
        ),
        PieceType.O to listOf(
            listOf(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1))
        ),
        PieceType.T to listOf(
            listOf(Point(-1, 0), Point(0, 0), Point(1, 0), Point(0, 1)),
            listOf(Point(0, -1), Point(0, 0), Point(0, 1), Point(1, 0)),
            listOf(Point(-1, 0), Point(0, 0), Point(1, 0), Point(0, -1)),
            listOf(Point(0, -1), Point(0, 0), Point(0, 1), Point(-1, 0))
        ),
        PieceType.L to listOf(
            listOf(Point(-1, 0), Point(0, 0), Point(1, 0), Point(1, 1)),
            listOf(Point(0, -1), Point(0, 0), Point(0, 1), Point(1, -1)),
            listOf(Point(-1, -1), Point(-1, 0), Point(0, 0), Point(1, 0)),
            listOf(Point(-1, 1), Point(0, -1), Point(0, 0), Point(0, 1))
        ),
        PieceType.J to listOf(
            listOf(Point(-1, 0), Point(0, 0), Point(1, 0), Point(-1, 1)),
            listOf(Point(0, -1), Point(0, 0), Point(0, 1), Point(1, 1)),
            listOf(Point(1, -1), Point(-1, 0), Point(0, 0), Point(1, 0)),
            listOf(Point(-1, -1), Point(0, -1), Point(0, 0), Point(0, 1))
        ),
        PieceType.S to listOf(
            listOf(Point(-1, 1), Point(0, 1), Point(0, 0), Point(1, 0)),
            listOf(Point(0, -1), Point(0, 0), Point(1, 0), Point(1, 1))
        ),
        PieceType.Z to listOf(
            listOf(Point(-1, 0), Point(0, 0), Point(0, 1), Point(1, 1)),
            listOf(Point(1, -1), Point(1, 0), Point(0, 0), Point(0, 1))
        )
    )
}
