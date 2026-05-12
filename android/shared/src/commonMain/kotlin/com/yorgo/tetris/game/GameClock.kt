package com.yorgo.tetris.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameClock(
    private val tickMillis: Long = 500L
) {
    private var job: Job? = null

    fun start(scope: CoroutineScope, onTick: () -> Unit) {
        stop()
        job = scope.launch {
            while (isActive) {
                delay(tickMillis)
                onTick()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
