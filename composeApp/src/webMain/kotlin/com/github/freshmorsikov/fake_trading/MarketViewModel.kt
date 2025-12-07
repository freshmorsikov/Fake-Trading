package com.github.freshmorsikov.fake_trading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.freshmorsikov.fake_trading.ai.NewsGenerator
import com.github.freshmorsikov.fake_trading.ai.TradingAnalytics
import com.github.freshmorsikov.fake_trading.ai.TradingAnalyticsGenerator
import com.github.freshmorsikov.fake_trading.api.SupabaseApi
import com.github.freshmorsikov.fake_trading.api.model.NewsRow
import com.github.freshmorsikov.fake_trading.api.model.StockRow
import com.github.freshmorsikov.fake_trading.api.model.TradeRow
import com.github.freshmorsikov.fake_trading.api.model.TraderRow
import com.github.freshmorsikov.fake_trading.api.model.TradingAnalyticsRow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val ADMIN = "admin"
private const val DAYS_COUNT = 5
private const val STEP_IN_DAY = 3
private const val NEWS_COUNT = 4

enum class DayTime {
    Morning,
    Noon,
    Evening,
}

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

    val isAdmin: Boolean = name.lowercase() == ADMIN
    val currentNews: List<NewsRow> = news.drop((day - 1) * NEWS_COUNT).take(NEWS_COUNT)
}

data class Stock(
    val name: String,
    val description: String,
    val priceBuy: Int,
    val priceSell: Int,
    val count: Int,
    val analytics: TradingAnalyticsRow?,
    val canBuy: Boolean,
    val isProcessing: Boolean,
) {
    val isBuyEnabled: Boolean = !isProcessing && canBuy
    val isSellEnabled: Boolean = !isProcessing && count > 0
}

class MarketViewModel() : ViewModel() {

    private val supabaseApi by lazy {
        SupabaseApi()
    }
    private val newsGenerator by lazy {
        NewsGenerator()
    }
    private val tradingAnalyticsGenerator by lazy {
        TradingAnalyticsGenerator()
    }

    private val _state = MutableStateFlow(
        MarketState(
            name = "",
            stepNumber = 0,
            dayCount = DAYS_COUNT,
            news = emptyList(),
            traders = emptyList(),
            stocks = emptyList(),
            balance = 0,
            isRefreshEnabled = true,
        )
    )
    val state = _state.asStateFlow()

    private fun isAdmin(): Boolean {
        return _state.value.name.lowercase() == ADMIN
    }

    init {
        subscribeToStep()
    }

    fun setName(name: String) {
        _state.update {
            it.copy(name = name)
        }

        subscribeToStocks(name = name)
        if (isAdmin()) {
            subscribeToNews()
            subscribeToTraders()
        } else {
            viewModelScope.launch {
                supabaseApi.checkTrader(name = name)
            }
            subscribeToBalance(name = name)
        }
    }

    fun goToPreviousStep() {
        viewModelScope.launch {
            supabaseApi.updateStep(step = _state.value.stepNumber - 1)
        }
    }

    fun goToNextStep() {
        viewModelScope.launch {
            supabaseApi.updateStep(step = _state.value.stepNumber + 1)
        }
    }

    fun generateNews() {
        _state.update {
            it.copy(isRefreshEnabled = false)
        }
        viewModelScope.launch {
            delay(5_000)
            _state.update {
                it.copy(isRefreshEnabled = true)
            }
        }

        viewModelScope.launch {
            val news = newsGenerator.generateNews(count = DAYS_COUNT * NEWS_COUNT * 2)
            if (news != null) {
                launch {
                    supabaseApi.updateNews(newsTitles = news)
                }
                launch {
                    updateTradingAnalytics(news = news)
                }
            }
        }
    }

    private suspend fun updateTradingAnalytics(news: List<String>) = coroutineScope {
        supabaseApi.deleteTradingAnalytics()

        val stocks = supabaseApi.getsStocksFlow().first()
        println("stocks: ${stocks.size}")

        for (day in 1..DAYS_COUNT) {
            launch {
                val newsPortion = news.drop((day - 1) * NEWS_COUNT).take(NEWS_COUNT)
                println("day: ${day}, newsPortion: ${newsPortion.size}")

                delay(day * 1_000L)
                val tradingAnalytics = tradingAnalyticsGenerator.generateTradingAnalytics(
                    stocks = stocks,
                    news = newsPortion
                )
                println("day: ${day}, tradingAnalytics: ${tradingAnalytics?.size}")

                tradingAnalytics?.let {
                    val tradingAnalyticsRows = stocks.mapIndexed { i, stock ->
                        stock.toTradingAnalytics(
                            tradingAnalytics = tradingAnalytics.find {
                                it.companyId == i
                            },
                            day = day,
                        )
                    }
                    supabaseApi.saveTradingAnalytics(tradingAnalytics = tradingAnalyticsRows)
                }
            }
        }
    }

    private fun StockRow.toTradingAnalytics(
        tradingAnalytics: TradingAnalytics?,
        day: Int,
    ): TradingAnalyticsRow {
        return TradingAnalyticsRow(
            stock = name,
            change = tradingAnalytics?.percentChange ?: 0,
            note = tradingAnalytics?.shortNote ?: "-",
            day = day,
        )
    }

    fun buyStock(stockName: String) {
        viewModelScope.launch {
            setProcessing(
                stockName = stockName,
                isProcessing = true,
            )
            supabaseApi.createTrade(
                traderName = _state.value.name,
                stockName = stockName,
                buy = true,
            )
            setProcessing(
                stockName = stockName,
                isProcessing = false,
            )
        }
    }

    fun sellStock(stockName: String) {
        viewModelScope.launch {
            setProcessing(
                stockName = stockName,
                isProcessing = true,
            )
            supabaseApi.createTrade(
                traderName = _state.value.name,
                stockName = stockName,
                buy = false,
            )
            setProcessing(
                stockName = stockName,
                isProcessing = false,
            )
        }
    }

    private fun setProcessing(stockName: String, isProcessing: Boolean) {
        _state.update {
            it.copy(
                stocks = it.stocks.map { stock ->
                    if (stockName == stock.name) {
                        stock.copy(isProcessing = isProcessing)
                    } else {
                        stock
                    }
                }
            )
        }
    }

    private fun subscribeToStep() {
        supabaseApi.getStepFlow()
            .onEach { step ->
                _state.update {
                    it.copy(stepNumber = step)
                }
            }.launchIn(viewModelScope)
    }

    private fun subscribeToNews() {
        supabaseApi.getNewsFlow()
            .onEach { news ->
                _state.update {
                    it.copy(news = news)
                }
            }.launchIn(viewModelScope)
    }

    private fun subscribeToTraders() {
        viewModelScope.launch {
            supabaseApi.getTraderFlow()
                .onEach { traders ->
                    _state.update {
                        it.copy(traders = traders)
                    }
                }.launchIn(viewModelScope)
        }
    }

    private fun subscribeToBalance(name: String) {
        getBalanceFlow(name = name)
            .onEach { balance ->
                _state.update {
                    it.copy(balance = balance)
                }
            }.launchIn(viewModelScope)
    }

    private fun subscribeToStocks(name: String) {
        val tradesFlow = if (isAdmin()) {
            flowOf(emptyList())
        } else {
            supabaseApi.getTradesFlow(name = name)
        }
        val balanceFlow = if (isAdmin()) {
            flowOf(0)
        } else {
            getBalanceFlow(name = name)
        }
        combine(
            supabaseApi.getsStocksFlow(),
            tradesFlow,
            balanceFlow,
            _state.map { it.day }.distinctUntilChanged(),
            _state.map { it.dayTime == DayTime.Evening }.distinctUntilChanged(),
            supabaseApi.getTradingAnalyticsFlow(),
        ) { values ->
            val stocks = values[0] as List<StockRow>
            val trades = values[1] as List<TradeRow>
            val balance = values[2] as Int
            val day = values[3] as Int
            val isEvening = values[4] as Boolean
            val tradingAnalytics = values[5] as List<TradingAnalyticsRow>

            if (_state.value.stocks.isEmpty()) {
                createNewList(
                    stocks = stocks,
                    trades = trades,
                    balance = balance,
                )
            } else {
                val dayAnalytics = tradingAnalytics.filter { analytics ->
                    isAdmin() && isEvening && analytics.day == day
                }
                updateStockList(
                    stocks = _state.value.stocks,
                    updatedStocks = stocks,
                    trades = trades,
                    analytics = dayAnalytics,
                    balance = balance,
                )
            }
        }.onEach { stocks ->
            _state.update {
                it.copy(stocks = stocks)
            }
        }.launchIn(viewModelScope)
    }

    private fun createNewList(
        stocks: List<StockRow>,
        trades: List<TradeRow>,
        balance: Int,
    ): List<Stock> {
        return stocks.map { stock ->
            Stock(
                name = stock.name,
                description = stock.description,
                priceBuy = stock.priceBuy,
                priceSell = stock.priceSell,
                count = calculateCount(
                    trades = trades,
                    stockName = stock.name,
                ),
                analytics = null,
                canBuy = balance > stock.priceBuy,
                isProcessing = false,
            )
        }
    }

    private fun updateStockList(
        stocks: List<Stock>,
        updatedStocks: List<StockRow>,
        trades: List<TradeRow>,
        analytics: List<TradingAnalyticsRow>,
        balance: Int,
    ): List<Stock> {
        return stocks.map { stock ->
            val stockAnalytics = analytics.find { it.stock == stock.name }
            val updatedStock = updatedStocks.find { it.name == stock.name }
            stock.copy(
                priceBuy = updatedStock?.priceBuy ?: stock.priceBuy,
                priceSell = updatedStock?.priceSell ?: stock.priceSell,
                count = calculateCount(
                    trades = trades,
                    stockName = stock.name,
                ),
                analytics = stockAnalytics?.takeIf { it.change != 0 },
                canBuy = balance > stock.priceBuy,
            )
        }
    }

    private fun calculateCount(
        trades: List<TradeRow>,
        stockName: String,
    ): Int {
        val boughtCount = trades.count { trade ->
            trade.stock == stockName && trade.buy
        }
        val soldCount = trades.count { trade ->
            trade.stock == stockName && !trade.buy
        }

        return boughtCount - soldCount
    }

    private fun getBalanceFlow(name: String): Flow<Int> {
        val balanceFlow = supabaseApi.getBalanceFlow(name = name)
        val tradesFlow = supabaseApi.getTradesFlow(name = name)
        return combine(
            balanceFlow,
            tradesFlow,
        ) { balance, trades ->
            balance + trades.sumOf { tradeRow ->
                if (tradeRow.buy) {
                    -tradeRow.price
                } else {
                    tradeRow.price
                }
            }
        }
    }

}