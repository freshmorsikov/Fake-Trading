package com.github.freshmorsikov.fake_trading.api.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsRow(
    val id: Int,
    val title: String,
)