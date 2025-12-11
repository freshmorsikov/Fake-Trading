package com.github.freshmorsikov.fake_trading

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.github.freshmorsikov.fake_trading.core.ui.RootContainer
import com.github.freshmorsikov.fake_trading.presentation.ui.MarketScreen

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        RootContainer {
            MarketScreen()
        }
    }
}