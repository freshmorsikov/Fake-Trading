package com.github.freshmorsikov.fake_trading.data

import com.github.freshmorsikov.fake_trading.api.SupabaseApi
import com.github.freshmorsikov.fake_trading.api.model.TradeRow
import com.github.freshmorsikov.fake_trading.api.model.TradingAnalyticsRow
import com.github.freshmorsikov.fake_trading.domain.model.CommonStock
import com.github.freshmorsikov.fake_trading.domain.model.CommonTradingAnalytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.math.roundToInt

private const val PRICE_CHANGE = 10
private const val SPREAD = 20

class StockRepository {

    private val supabaseApi by lazy {
        SupabaseApi()
    }

    fun getStocksFlow(): Flow<List<CommonStock>> {
        return combine(
            supabaseApi.getsStocksFlow(),
            supabaseApi.getTradesFlow(),
            supabaseApi.getTradingAnalyticsFlow(),
            supabaseApi.getStepFlow(),
        ) { stocks, trades, analytics, step ->
            stocks.map { stock ->
                val tradesForStock = trades.filter { trade ->
                    trade.stock == stock.name
                }
                val analyticsForStock = analytics.filter {
                    it.stock == stock.name
                }
                val priceBuy = calculatePrice(
                    basePrice = stock.priceBuy,
                    trades = tradesForStock,
                    analytics = analyticsForStock,
                    step = step,
                )

                CommonStock(
                    name = stock.name,
                    description = stock.description,
                    priceBuy = priceBuy,
                    priceSell = priceBuy - SPREAD,
                    analytics = analyticsForStock.map {
                        CommonTradingAnalytics(
                            change = it.change,
                            note = it.note,
                            step = it.step,
                        )
                    },
                )
            }
        }
    }

    suspend fun getStockByName(name: String): CommonStock? {
        val stock = supabaseApi.getStockByName(stockName = name) ?: return null
        val trades = supabaseApi.getTradesByName(stockName = name)
        val analytics = supabaseApi.getTradingAnalyticsByName(stockName = name)
        val step = supabaseApi.getStep() ?: 0
        val priceBuy = calculatePrice(
            basePrice = stock.priceBuy,
            trades = trades,
            analytics = analytics,
            step = step,
        )

        return CommonStock(
            name = stock.name,
            description = stock.description,
            priceBuy = priceBuy,
            priceSell = priceBuy - SPREAD,
            analytics = analytics.map {
                CommonTradingAnalytics(
                    change = it.change,
                    note = it.note,
                    step = it.step,
                )
            },
        )
    }

    private fun calculatePrice(
        basePrice: Int,
        trades: List<TradeRow>,
        analytics: List<TradingAnalyticsRow>,
        step: Int,
    ): Int {
        var price = basePrice.toDouble()

        val tradesByStep = trades.groupBy { trade -> trade.step }
        val analyticsByStep = analytics.groupBy { it.step }

        for (i in 0..step) {
            val tradesChangeSum = tradesByStep[i]?.sumOf { trade ->
                if (trade.buy) PRICE_CHANGE else -PRICE_CHANGE
            } ?: 0
            price += tradesChangeSum

            val analyticsChange = analyticsByStep[i]?.firstOrNull()?.change ?: 0
            price *= (1.0 + analyticsChange / 100.0)
        }

        return price.roundToInt()
    }

}