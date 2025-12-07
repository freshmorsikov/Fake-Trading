package com.github.freshmorsikov.fake_trading

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.github.freshmorsikov.fake_trading.presentation.MarketScreen

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        MarketScreen()
    }
}