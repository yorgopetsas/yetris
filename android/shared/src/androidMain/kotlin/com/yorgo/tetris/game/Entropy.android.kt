package com.yorgo.tetris.game

internal actual fun entropySeed(): Long =
    System.nanoTime() xor kotlin.random.Random.Default.nextLong()
