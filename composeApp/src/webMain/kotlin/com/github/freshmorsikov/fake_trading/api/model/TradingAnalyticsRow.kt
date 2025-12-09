package com.github.freshmorsikov.fake_trading.api.model

import kotlinx.serialization.Serializable

@Serializable
data class TradingAnalyticsRow(
    val id: Int? = null,
    val stock: String,
    val change: Int,
    val note: String,
    val step: Int,
)