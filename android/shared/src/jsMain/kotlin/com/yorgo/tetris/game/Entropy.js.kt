package com.yorgo.tetris.game

internal actual fun entropySeed(): Long {
    val t = kotlin.js.Date.now().toLong()
    return t xor kotlin.random.Random.Default.nextLong()
}
