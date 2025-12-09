package com.github.freshmorsikov.fake_trading.api.model

import kotlinx.serialization.Serializable

@Serializable
data class TradeRow(
    val id: Int? = null,
    val trader: String,
    val stock: String,
    val price: Int,
    val buy: Boolean,
    val step: Int,
)