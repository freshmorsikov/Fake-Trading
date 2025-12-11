package com.github.freshmorsikov.fake_trading.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun rememberButtonState(): ButtonState {
    val scope = rememberCoroutineScope()
    return remember { ButtonState(scope = scope) }
}

class ButtonState(private val scope: CoroutineScope) {

    var isEnabled by mutableStateOf(true)
        private set

    fun setClicked() {
        isEnabled = false
        scope.launch {
            delay(1_000)
            isEnabled = true
        }
    }
}