package com.yorgo.tetris.game

import com.yorgo.tetris.domain.PieceType
import kotlin.test.Test
import kotlin.test.assertEquals

class PieceGeneratorQueueTest {
    @Test
    fun restoreQueue_preservesSpawnOrder() {
        val gen = PieceGenerator()
        gen.resetSession()
        val expected = buildList {
            repeat(5) { add(gen.next().type) }
        }
        val queue = gen.snapshotQueue()
        val restored = PieceGenerator()
        restored.restoreQueue(queue)
        val after = buildList {
            repeat(5) { add(restored.next().type) }
        }
        assertEquals(expected, after)
    }
}
