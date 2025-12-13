package com.github.freshmorsikov.fake_trading.domain.model

data class Stock(
    val name: String,
    val description: String,
    val priceBuy: Int,
    val priceSell: Int,
    val analytics: List<CommonTradingAnalytics>,
)