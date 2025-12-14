package com.github.freshmorsikov.fake_trading.presentation.model

import com.github.freshmorsikov.fake_trading.domain.model.TraderName

const val STEP_IN_DAY = 3
const val DAYS_COUNT = 5
const val NEWS_COUNT = 4
const val CURRENCY = "₣m"

data class MarketState(
    val traderName: TraderName,
    val step: StepUi,
    val news: List<NewsUi>,
    val traders: List<TraderUi>,
    val stocks: List<StockUi>,
    val balance: BalanceUi?,
    val isRefreshEnabled: Boolean,
) {
    val isTradingAvailable = step.dayTime == DayTime.Noon
    val currentNews: List<NewsUi> =
        news
            .drop((step.day - 1) * NEWS_COUNT)
            .take(NEWS_COUNT)
            .sortedBy { it.hours }
}

data class BalanceUi(
    val cash: Int,
    val stocks: Int,
) {
    val total: Int = cash + stocks
}

data class StepUi(
    val number: Int,
) {
    val isPreviousStepAvailable = number > 0
    val isNextStepAvailable = number < DAYS_COUNT * STEP_IN_DAY - 1

    val day: Int = number / STEP_IN_DAY + 1
    val dayTime: DayTime = when (number % STEP_IN_DAY) {
        0 -> DayTime.Morning
        1 -> DayTime.Noon
        else -> DayTime.Evening
    }
    val progress: Float = number.toFloat() / (DAYS_COUNT * STEP_IN_DAY)
}

data class TraderUi(
    val name: String,
    val balance: Int,
)

data class NewsUi(
    val title: String,
    val hours: Int,
    val likes: Int,
    val comments: Int,
    val views: Int,
)