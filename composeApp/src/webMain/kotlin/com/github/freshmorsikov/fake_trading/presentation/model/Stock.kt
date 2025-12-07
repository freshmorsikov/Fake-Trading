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
    val isProcessing: Boolean,
) {
    val isBuyEnabled: Boolean = !isProcessing && canBuy
    val isSellEnabled: Boolean = !isProcessing && count > 0
}