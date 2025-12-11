package com.github.freshmorsikov.fake_trading.core.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val LocalCompact = staticCompositionLocalOf<Boolean> { error("LocalCompact not present") }

@Composable
fun RootContainer(content: @Composable () -> Unit) {
    BoxWithConstraints {
        val compact = maxWidth < 600.dp
        CompositionLocalProvider(
            LocalCompact provides compact
        ) {
            content()
        }
    }
}