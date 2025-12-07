package com.github.freshmorsikov.fake_trading.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockRow(
    val name: String,
    val description: String,
    @SerialName("price_buy") val priceBuy: Int,
    @SerialName("price_sell") val priceSell: Int,
)
