package com.github.freshmorsikov.fake_trading.presentation.model

import com.github.freshmorsikov.fake_trading.domain.model.Step
import com.github.freshmorsikov.fake_trading.domain.model.TraderName

const val STEP_IN_DAY = 3
const val DAYS_COUNT = 5
const val NEWS_COUNT = 4
const val CURRENCY = "₣m"

data class MarketState(
    val traderName: TraderName,
    val step: Step,
    val news: List<NewsUi>,
    val traders: List<TraderUi>,
    val stocks: List<StockUi>,
    val balance: BalanceUi?,
    val isRefreshEnabled: Boolean,
) {
    val currentNews: List<NewsUi> = news
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