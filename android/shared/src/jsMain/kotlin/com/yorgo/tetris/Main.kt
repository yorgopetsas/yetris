package com.yorgo.tetris

import com.yorgo.tetris.data.LocalStorageBestScoreStore
import com.yorgo.tetris.domain.BoardConfig
import com.yorgo.tetris.domain.InputActionType
import com.yorgo.tetris.domain.PieceType
import com.yorgo.tetris.domain.SessionStatus
import com.yorgo.tetris.game.CollisionRules
import com.yorgo.tetris.game.GameEngine
import com.yorgo.tetris.leaderboard.WebLeaderboardConfig
import com.yorgo.tetris.leaderboard.fetchTopScores
import com.yorgo.tetris.leaderboard.submitScore
import kotlinx.browser.console
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.events.KeyboardEvent

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

    val cellPx = 27
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
            "Score: ${session.score} | Best on this device: ${store.readBestScore()}$bestLabel"

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

    fun dispatchGameplay(action: InputActionType) {
        if (engine.session.status == SessionStatus.RUNNING) {
            engine.dispatch(action)
        }
    }

    canvas.tabIndex = 0
    canvas.focus()

    window.onkeydown = { event ->
        val keyEvent = event as? KeyboardEvent
        if (keyEvent != null && document.activeElement != nameInput) {
            val action = when (keyEvent.key) {
                "ArrowLeft" -> InputActionType.MOVE_LEFT
                "ArrowRight" -> InputActionType.MOVE_RIGHT
                "ArrowUp" -> InputActionType.ROTATE
                "ArrowDown" -> InputActionType.SOFT_DROP
                else -> null
            }
            if (action != null) {
                keyEvent.preventDefault()
                dispatchGameplay(action)
            }
        }
        null
    }

    val leaderboardModal = document.getElementById("leaderboardModal") as HTMLDivElement
    val leaderboardBody = document.getElementById("leaderboardBody") as HTMLDivElement

    fun showLeaderboardModal() {
        leaderboardModal.style.display = "flex"
        leaderboardModal.setAttribute("aria-hidden", "false")
        leaderboardBody.textContent = "Loading…"
        scope.launch {
            if (!WebLeaderboardConfig.isConfigured()) {
                leaderboardBody.textContent =
                    "Global leaderboard is not configured. Set YETRIS_LEADERBOARD_URL and " +
                        "YETRIS_LEADERBOARD_TOKEN in index.html (same values as android/local.properties)."
                return@launch
            }
            val entries = fetchTopScores(limit = 10)
            if (entries.isEmpty()) {
                leaderboardBody.textContent =
                    "No scores yet. Beat your personal best, enter your name, and post to the shared list."
            } else {
                leaderboardBody.innerHTML = "<ol id=\"leaderboardList\">" +
                    entries.joinToString("") { e ->
                        "<li>${escapeHtml(e.name)} — ${e.score}</li>"
                    } +
                    "</ol>"
            }
        }
    }

    fun hideLeaderboardModal() {
        leaderboardModal.style.display = "none"
        leaderboardModal.setAttribute("aria-hidden", "true")
    }

    wire("btnHighScores") { showLeaderboardModal() }
    wire("leaderboardClose") { hideLeaderboardModal() }

    (document.getElementById("nameSave") as HTMLButtonElement).onclick = {
        val trimmed = nameInput.value.trim().take(LocalStorageBestScoreStore.MAX_NAME_LEN)
            .ifEmpty { "Anonymous" }
        val session = engine.session
        if (session.status == SessionStatus.GAME_OVER &&
            session.score > store.readBestScore()
        ) {
            store.writeBest(session.score, trimmed)
            scope.launch {
                if (!submitScore(trimmed, session.score)) {
                    console.error("Could not save score to shared leaderboard. Check Apps Script token and redeploy.")
                }
            }
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

private fun escapeHtml(text: String): String =
    text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
