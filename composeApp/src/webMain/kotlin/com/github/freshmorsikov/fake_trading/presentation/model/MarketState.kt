package com.github.freshmorsikov.fake_trading.presentation.model

import com.github.freshmorsikov.fake_trading.api.model.NewsRow
import com.github.freshmorsikov.fake_trading.api.model.TraderRow

const val STEP_IN_DAY = 3
const val DAYS_COUNT = 5
const val NEWS_COUNT = 4
private const val ADMIN = "admin"
const val CURRENCY = "₣m"

data class MarketState(
    val name: String,
    val stepNumber: Int,
    val dayCount: Int,
    val news: List<NewsRow>,
    val traders: List<TraderRow>,
    val stocks: List<Stock>,
    val balance: Int,
    val isRefreshEnabled: Boolean,
) {
    val day: Int = stepNumber / STEP_IN_DAY + 1
    val dayTime: DayTime = when (stepNumber % STEP_IN_DAY) {
        0 -> DayTime.Morning
        1 -> DayTime.Noon
        else -> DayTime.Evening
    }
    val isPreviousStepAvailable = stepNumber > 0
    val isNextStepAvailable = stepNumber < DAYS_COUNT * STEP_IN_DAY - 1
    val isTradingAvailable = dayTime == DayTime.Noon

    val isAdmin: Boolean = name.lowercase() == ADMIN
    val currentNews: List<NewsRow> = news.drop((day - 1) * NEWS_COUNT).take(NEWS_COUNT)
}