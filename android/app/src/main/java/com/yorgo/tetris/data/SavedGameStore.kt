package com.yorgo.tetris.data

import com.yorgo.tetris.domain.SavedGameSnapshot

interface SavedGameStore {
    fun hasSavedGame(): Boolean
    fun load(): SavedGameSnapshot?
    fun save(snapshot: SavedGameSnapshot)
    fun clear()
}
