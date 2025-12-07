package com.github.freshmorsikov.fake_trading.api.model

import kotlinx.serialization.Serializable

@Serializable
data class TraderRow(
    val name: String,
    val balance: Int,
)