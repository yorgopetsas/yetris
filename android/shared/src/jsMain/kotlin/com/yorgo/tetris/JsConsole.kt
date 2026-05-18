package com.yorgo.tetris

import kotlinx.browser.window

internal fun jsConsoleError(message: String) {
    window.asDynamic().console.error(message)
}
