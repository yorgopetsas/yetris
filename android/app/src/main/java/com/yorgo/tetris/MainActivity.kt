package com.yorgo.tetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.yorgo.tetris.ui.AppLifecycleObserver
import com.yorgo.tetris.ui.GameScreen
import com.yorgo.tetris.ui.GameViewModel

class MainActivity : ComponentActivity() {
    private val vm: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(AppLifecycleObserver(vm))
        setContent { GameScreen(vm) }
    }
}
