package com.github.freshmorsikov.fake_trading.domain.model

private const val ADMIN = "admin"

data class TraderName(val name: String) {
    val isNone: Boolean = name.isEmpty()
    val isAdmin: Boolean = name.lowercase() == ADMIN

    companion object {
        val None = TraderName(name = "")
    }
}