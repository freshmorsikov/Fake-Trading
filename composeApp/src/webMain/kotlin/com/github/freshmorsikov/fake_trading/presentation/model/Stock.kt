package com.github.freshmorsikov.fake_trading.presentation.model

import com.github.freshmorsikov.fake_trading.api.model.TradingAnalyticsRow

data class Stock(
    val name: String,
    val description: String,
    val priceBuy: Int,
    val priceSell: Int,
    val count: Int,
    val analytics: TradingAnalyticsRow?,
    val canBuy: Boolean,
) {
    val isBuyEnabled: Boolean = canBuy
    val isSellEnabled: Boolean = count > 0
}