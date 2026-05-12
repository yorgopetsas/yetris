package com.yorgo.tetris.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yorgo.tetris.domain.InputActionType

class AppLifecycleObserver(
    private val vm: GameViewModel
) : DefaultLifecycleObserver {
    override fun onPause(owner: LifecycleOwner) {
        vm.onAction(InputActionType.PAUSE)
    }

    override fun onResume(owner: LifecycleOwner) {
        vm.onAction(InputActionType.RESUME)
    }
}
