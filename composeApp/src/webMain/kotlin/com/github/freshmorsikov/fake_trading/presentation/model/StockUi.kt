package com.github.freshmorsikov.fake_trading.presentation.model

import com.github.freshmorsikov.fake_trading.domain.model.CommonTradingAnalytics

data class StockUi(
    val name: String,
    val description: String,
    val priceBuy: Int,
    val priceSell: Int,
    val icon: String,
    val count: Int,
    val analytics: CommonTradingAnalytics?,
    val haveEnoughCash: Boolean,
    val isTradingAvailable: Boolean,
) {
    val isBuyEnabled: Boolean = haveEnoughCash && isTradingAvailable
    val isSellEnabled: Boolean = count > 0 && isTradingAvailable
}