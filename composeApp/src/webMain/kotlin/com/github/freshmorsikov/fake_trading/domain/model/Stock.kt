package com.github.freshmorsikov.fake_trading.domain.model

data class Stock(
    val name: String,
    val description: String,
    val priceBuy: Int,
    val priceSell: Int,
    val icon: String,
    val analytics: List<CommonTradingAnalytics>,
)