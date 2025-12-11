package com.github.freshmorsikov.fake_trading.presentation.model

import com.github.freshmorsikov.fake_trading.domain.model.CommonTradingAnalytics

data class StockUi(
    val name: String,
    val description: String,
    val priceBuy: Int,
    val priceSell: Int,
    val count: Int,
    val analytics: CommonTradingAnalytics?,
    val canBuy: Boolean,
) {
    val isBuyEnabled: Boolean = canBuy
    val isSellEnabled: Boolean = count > 0
}