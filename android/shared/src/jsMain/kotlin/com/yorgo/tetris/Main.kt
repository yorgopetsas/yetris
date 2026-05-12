package com.yorgo.tetris

import com.yorgo.tetris.data.LocalStorageBestScoreStore
import com.yorgo.tetris.domain.BoardConfig
import com.yorgo.tetris.domain.InputActionType
import com.yorgo.tetris.domain.PieceType
import com.yorgo.tetris.domain.SessionStatus
import com.yorgo.tetris.game.CollisionRules
import com.yorgo.tetris.game.GameEngine
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLParagraphElement

private val pieceColors: Map<PieceType, String> = mapOf(
    PieceType.I to "#00f0f0",
    PieceType.O to "#f0f000",
    PieceType.T to "#a000f0",
    PieceType.L to "#f0a000",
    PieceType.J to "#0000f0",
    PieceType.S to "#00f000",
    PieceType.Z to "#f00000"
)

fun main() {
    val scope = MainScope()
    val store = LocalStorageBestScoreStore()
    val engine = GameEngine()

    val canvas = document.getElementById("gameCanvas") as HTMLCanvasElement
    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
    val scoreLine = document.getElementById("scoreLine") as HTMLParagraphElement
    val statusLine = document.getElementById("status") as HTMLParagraphElement
    val nameEntry = document.getElementById("nameEntry") as HTMLDivElement
    val nameInput = document.getElementById("nameInput") as HTMLInputElement

    val cellPx = 18
    canvas.width = BoardConfig.WIDTH * cellPx
    canvas.height = BoardConfig.HEIGHT * cellPx

    fun render() {
        val session = engine.session
        val prevBest = store.readBestScore()
        val needsNameEntry =
            session.status == SessionStatus.GAME_OVER && session.score > prevBest

        val rows = if (session.board.cells.isEmpty()) {
            List(BoardConfig.HEIGHT) { List(BoardConfig.WIDTH) { false } }
        } else {
            session.board.cells
        }
        val activeSet = session.activePiece?.let { piece ->
            CollisionRules.occupiedCells(piece).map { it.x to it.y }.toSet()
        } ?: emptySet()

        ctx.fillStyle = "#f5f5f5"
        ctx.fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        for (y in rows.indices) {
            for (x in rows[y].indices) {
                val left = x * cellPx.toDouble()
                val top = y * cellPx.toDouble()
                val active = activeSet.contains(x to y)
                val filled = rows[y][x]
                ctx.fillStyle = when {
                    active -> session.activePiece?.type?.let { pieceColors[it] } ?: "#6200EE"
                    filled -> "#424242"
                    else -> "#eeeeee"
                }
                ctx.fillRect(left, top, cellPx.toDouble(), cellPx.toDouble())
                ctx.strokeStyle = "#00000033"
                ctx.strokeRect(left, top, cellPx.toDouble(), cellPx.toDouble())
            }
        }

        val bestName = store.readBestPlayerName()
        val bestLabel = if (bestName.isNotEmpty()) " ($bestName)" else ""
        scoreLine.textContent =
            "Score: ${session.score} | Best: ${store.readBestScore()}$bestLabel"

        statusLine.textContent = when (session.status) {
            SessionStatus.READY -> "Ready"
            SessionStatus.RUNNING -> "Playing"
            SessionStatus.PAUSED -> "Paused"
            SessionStatus.GAME_OVER -> "Game over"
        }

        nameEntry.style.display = if (needsNameEntry) "block" else "none"
        if (needsNameEntry) {
            nameInput.focus()
        }
    }

    engine.setStateListener { render() }

    fun wire(id: String, action: () -> Unit) {
        (document.getElementById(id) as HTMLButtonElement).onclick = { action() }
    }

    wire("btnLeft") { engine.dispatch(InputActionType.MOVE_LEFT) }
    wire("btnRight") { engine.dispatch(InputActionType.MOVE_RIGHT) }
    wire("btnRot") { engine.dispatch(InputActionType.ROTATE) }
    wire("btnDrop") { engine.dispatch(InputActionType.SOFT_DROP) }
    wire("btnRestart") {
        engine.dispatch(InputActionType.RESTART)
    }

    (document.getElementById("nameSave") as HTMLButtonElement).onclick = {
        val trimmed = nameInput.value.trim().take(LocalStorageBestScoreStore.MAX_NAME_LEN)
            .ifEmpty { "Anonymous" }
        val session = engine.session
        if (session.status == SessionStatus.GAME_OVER &&
            session.score > store.readBestScore()
        ) {
            store.writeBest(session.score, trimmed)
        }
        engine.dispatch(InputActionType.RESTART)
        render()
    }

    window.onbeforeunload = {
        scope.cancel()
        null
    }

    render()
    engine.start(scope)
}
