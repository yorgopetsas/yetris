package com.yorgo.tetris.data

import android.content.Context
import com.yorgo.tetris.domain.GameBoard
import com.yorgo.tetris.domain.GameSession
import com.yorgo.tetris.domain.Piece
import com.yorgo.tetris.domain.PieceType
import com.yorgo.tetris.domain.Point
import com.yorgo.tetris.domain.SavedGameSnapshot
import com.yorgo.tetris.domain.SessionStatus
import org.json.JSONArray
import org.json.JSONObject

class SharedPreferencesSavedGameStore(context: Context) : SavedGameStore {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun hasSavedGame(): Boolean = prefs.contains(KEY_JSON)

    override fun load(): SavedGameSnapshot? {
        val raw = prefs.getString(KEY_JSON, null) ?: return null
        return runCatching { decode(raw) }.getOrNull()
    }

    override fun save(snapshot: SavedGameSnapshot) {
        prefs.edit().putString(KEY_JSON, encode(snapshot)).apply()
    }

    override fun clear() {
        prefs.edit().remove(KEY_JSON).apply()
    }

    private fun encode(snapshot: SavedGameSnapshot): String {
        val session = snapshot.session
        val board = JSONArray()
        session.board.cells.forEach { row ->
            val rowArr = JSONArray()
            row.forEach { rowArr.put(it) }
            board.put(rowArr)
        }
        val bag = JSONArray()
        snapshot.bagQueue.forEach { bag.put(it.name) }
        val piece = session.activePiece
        val pieceJson = if (piece != null) {
            JSONObject()
                .put("type", piece.type.name)
                .put("originX", piece.origin.x)
                .put("originY", piece.origin.y)
                .put("rotation", piece.rotation)
        } else {
            null
        }
        return JSONObject()
            .put("version", snapshot.version)
            .put("savedAt", snapshot.savedAtEpochMillis)
            .put("status", session.status.name)
            .put("score", session.score)
            .put("linesCleared", session.linesCleared)
            .put("level", session.level)
            .put("tick", session.tick)
            .put("board", board)
            .put("bagQueue", bag)
            .put("activePiece", pieceJson)
            .toString()
    }

    private fun decode(raw: String): SavedGameSnapshot {
        val root = JSONObject(raw)
        val version = root.getInt("version")
        require(version == SavedGameSnapshot.CURRENT_VERSION) { "Unsupported save version: $version" }
        val boardCells = mutableListOf<List<Boolean>>()
        val boardArr = root.getJSONArray("board")
        for (y in 0 until boardArr.length()) {
            val rowArr = boardArr.getJSONArray(y)
            val row = mutableListOf<Boolean>()
            for (x in 0 until rowArr.length()) {
                row.add(rowArr.getBoolean(x))
            }
            boardCells.add(row)
        }
        val bagQueue = mutableListOf<PieceType>()
        val bagArr = root.getJSONArray("bagQueue")
        for (i in 0 until bagArr.length()) {
            bagQueue.add(PieceType.valueOf(bagArr.getString(i)))
        }
        val activePiece = if (root.isNull("activePiece")) {
            null
        } else {
            val p = root.getJSONObject("activePiece")
            Piece(
                type = PieceType.valueOf(p.getString("type")),
                origin = Point(p.getInt("originX"), p.getInt("originY")),
                rotation = p.getInt("rotation")
            )
        }
        val session = GameSession(
            status = SessionStatus.valueOf(root.getString("status")),
            score = root.getInt("score"),
            linesCleared = root.getInt("linesCleared"),
            level = root.getInt("level"),
            tick = root.getLong("tick"),
            board = GameBoard(cells = boardCells),
            activePiece = activePiece
        )
        return SavedGameSnapshot(
            version = version,
            savedAtEpochMillis = root.getLong("savedAt"),
            session = session,
            bagQueue = bagQueue
        )
    }

    companion object {
        private const val PREFS_NAME = "tetris_saved_game"
        private const val KEY_JSON = "saved_game_json"
    }
}
