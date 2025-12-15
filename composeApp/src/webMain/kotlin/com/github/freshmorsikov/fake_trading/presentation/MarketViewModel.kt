package com.github.freshmorsikov.fake_trading.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.freshmorsikov.fake_trading.ai.NewsGenerator
import com.github.freshmorsikov.fake_trading.ai.TradingAnalytics
import com.github.freshmorsikov.fake_trading.ai.TradingAnalyticsGenerator
import com.github.freshmorsikov.fake_trading.api.SupabaseApi
import com.github.freshmorsikov.fake_trading.api.model.StockRow
import com.github.freshmorsikov.fake_trading.api.model.TradingAnalyticsRow
import com.github.freshmorsikov.fake_trading.data.StockRepository
import com.github.freshmorsikov.fake_trading.domain.GetStepFlow
import com.github.freshmorsikov.fake_trading.domain.GetStockCountListFlowUseCase
import com.github.freshmorsikov.fake_trading.domain.model.Step
import com.github.freshmorsikov.fake_trading.domain.model.TraderName
import com.github.freshmorsikov.fake_trading.presentation.model.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.collections.map
import kotlin.random.Random

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
    private val stockRepository by lazy {
        StockRepository()
    }
    private val getStockCountListFlowUseCase by lazy {
        GetStockCountListFlowUseCase()
    }
    private val getStepFlow by lazy {
        GetStepFlow()
    }

    private val _state = MutableStateFlow(
        MarketState(
            traderName = TraderName.None,
            step = Step(number = 0),
            news = emptyList(),
            traders = emptyList(),
            stocks = emptyList(),
            balance = null,
            isRefreshEnabled = true,
        )
    )
    val state = _state.asStateFlow()

    init {
        subscribeToStep()
    }

    fun setName(name: String) {
        val traderName = TraderName(name = name)
        _state.update {
            it.copy(traderName = traderName)
        }

        subscribeToStocks(traderName = traderName)
        if (_state.value.traderName.isAdmin) {
            subscribeToNews()
            subscribeToTraders()
        } else {
            viewModelScope.launch {
                supabaseApi.checkTrader(name = traderName.name)
            }
            subscribeToBalance(traderName = traderName)
        }
    }

    fun goToPreviousStep() {
        viewModelScope.launch {
            supabaseApi.updateStep(step = _state.value.step.number - 1)
        }
    }

    fun goToNextStep() {
        viewModelScope.launch {
            supabaseApi.updateStep(step = _state.value.step.number + 1)
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

        for (step in 2 until (DAYS_COUNT * STEP_IN_DAY) step STEP_IN_DAY) {
            launch {
                val newsPortion = news.drop(step).take(NEWS_COUNT)

                delay(step * 500L)
                val tradingAnalytics = tradingAnalyticsGenerator.generateTradingAnalytics(
                    stocks = stocks,
                    news = newsPortion
                )

                tradingAnalytics?.let {
                    val tradingAnalyticsRows = stocks.mapIndexed { i, stock ->
                        stock.toTradingAnalytics(
                            tradingAnalytics = tradingAnalytics.find {
                                it.companyId == i
                            },
                            step = step,
                        )
                    }
                    supabaseApi.saveTradingAnalytics(tradingAnalytics = tradingAnalyticsRows)
                }
            }
        }
    }

    private fun StockRow.toTradingAnalytics(
        tradingAnalytics: TradingAnalytics?,
        step: Int,
    ): TradingAnalyticsRow {
        return TradingAnalyticsRow(
            stock = name,
            change = tradingAnalytics?.percentChange ?: 0,
            note = tradingAnalytics?.shortNote ?: "-",
            step = step,
        )
    }

    fun buyStock(stockName: String) {
        viewModelScope.launch {
            val stock = stockRepository.getStockByName(name = stockName) ?: return@launch
            supabaseApi.createTrade(
                stockName = stockName,
                traderName = _state.value.traderName.name,
                buy = true,
                price = stock.priceBuy,
                step = _state.value.step.number,
            )
        }
    }

    fun sellStock(stockName: String) {
        viewModelScope.launch {
            val stock = stockRepository.getStockByName(name = stockName) ?: return@launch
            supabaseApi.createTrade(
                stockName = stockName,
                traderName = _state.value.traderName.name,
                buy = false,
                price = stock.priceSell,
                step = _state.value.step.number,
            )
        }
    }

    private fun subscribeToStep() {
        getStepFlow().onEach { step ->
            _state.update {
                it.copy(step = step)
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToNews() {
        supabaseApi.getNewsFlow()
            .onEach { news ->
                val newsUi = news.map {
                    NewsUi(
                        title = it.title,
                        hours = Random.nextInt(1, 10),
                        likes = Random.nextInt(20, 200),
                        comments = Random.nextInt(1, 20),
                        views = Random.nextInt(2, 10) * 100,
                    )
                }
                _state.update {
                    it.copy(news = newsUi)
                }
            }.launchIn(viewModelScope)
    }

    private fun subscribeToTraders() {
        viewModelScope.launch {
            supabaseApi.getTraderFlow()
                .onEach { traders ->
                    val uiTraders = traders.map { trader ->
                        TraderUi(
                            name = trader.name,
                            balance = trader.balance,
                        )
                    }
                    _state.update {
                        it.copy(traders = uiTraders)
                    }
                }.launchIn(viewModelScope)
        }
    }

    private fun subscribeToBalance(traderName: TraderName) {
        combine(
            getCashFlow(traderName = traderName),
            getStockValueFlow(traderName = traderName),
        ) { cash, stocks ->
            _state.update {
                it.copy(
                    balance = BalanceUi(
                        cash = cash,
                        stocks = stocks,
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeToStocks(traderName: TraderName) {
        combine(
            getStockCountListFlowUseCase(traderName = traderName),
            getCashFlow(traderName = traderName),
            getStepFlow(),
        ) { stockCountList, cash, step ->
            stockCountList.map { stockCount ->
                StockUi(
                    name = stockCount.stock.name,
                    description = stockCount.stock.description,
                    priceBuy = stockCount.stock.priceBuy,
                    priceSell = stockCount.stock.priceSell,
                    count = stockCount.count,
                    analytics = stockCount.stock.analytics.find { analytics ->
                        analytics.step == step.number
                    }?.takeIf { analytics ->
                        analytics.change != 0
                    },
                    haveEnoughCash = cash > stockCount.stock.priceBuy,
                    isTradingAvailable = step.dayTime == DayTime.Noon,
                )
            }
        }.onEach { stocks ->
            _state.update {
                it.copy(stocks = stocks)
            }
        }.launchIn(viewModelScope)
    }

    private fun getCashFlow(traderName: TraderName): Flow<Int> {
        val balanceFlow = if (traderName.isAdmin) {
            flowOf(0)
        } else {
            supabaseApi.getBalanceFlow(traderName = traderName.name)
        }
        val tradesFlow = supabaseApi.getTradesFlow(traderName = traderName.name)
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

    private fun getStockValueFlow(traderName: TraderName): Flow<Int> {
        val stockCountFlow = getStockCountListFlowUseCase(traderName = traderName)
        return stockCountFlow.map { stockCounts ->
            stockCounts.sumOf { stockCount ->
                stockCount.count * stockCount.stock.priceSell
            }
        }
    }

}