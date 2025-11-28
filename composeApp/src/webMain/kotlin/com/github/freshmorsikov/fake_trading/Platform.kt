package com.github.freshmorsikov.fake_trading

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform