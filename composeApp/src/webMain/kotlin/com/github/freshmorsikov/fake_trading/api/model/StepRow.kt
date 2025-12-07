package com.github.freshmorsikov.fake_trading.api.model

import kotlinx.serialization.Serializable

@Serializable
data class StepRow(
    val id: Int,
    val step: Int,
)