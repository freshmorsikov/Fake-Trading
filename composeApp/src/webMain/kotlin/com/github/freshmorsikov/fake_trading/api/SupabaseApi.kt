package com.github.freshmorsikov.fake_trading.api

import com.github.freshmorsikov.fake_trading.api.model.NewsRow
import com.github.freshmorsikov.fake_trading.api.model.StepRow
import com.github.freshmorsikov.fake_trading.api.model.StockRow
import com.github.freshmorsikov.fake_trading.api.model.TradeRow
import com.github.freshmorsikov.fake_trading.api.model.TraderRow
import com.github.freshmorsikov.fake_trading.api.model.TradingAnalyticsRow
import fake_trading.composeApp.BuildConfig
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private const val INITIAL_BALANCE = 1000

@OptIn(SupabaseExperimental::class, FlowPreview::class)
class SupabaseApi {

    private val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_API_KEY,
    ) {
        install(Postgrest)
        install(Realtime)
        defaultSerializer = KotlinXSerializer(Json {})
    }

    fun getStepFlow(): Flow<Int> {
        return supabase.from(table = "step")
            .selectSingleValueAsFlow(primaryKey = StepRow::id) {
                StepRow::id eq 1
            }.map { it.step }
    }

    suspend fun updateStep(step: Int) {
        supabase.from("step").update(
            update = { StepRow::step setTo step }
        ) {
            filter { StepRow::id eq 1 }
        }
    }

    suspend fun checkTrader(name: String) {
        val trader = supabase.from(table = "trader")
            .select {
                filter { TraderRow::name eq name }
            }.decodeSingleOrNull<TraderRow>()
        if (trader == null) {
            supabase.from(table = "trader")
                .insert(
                    value = TraderRow(
                        name = name,
                        balance = INITIAL_BALANCE,
                    )
                )
        }
    }

    fun getTraderFlow(): Flow<List<TraderRow>> {
        return supabase
            .from("trader")
            .selectAsFlow(TraderRow::name)
    }

    fun getBalanceFlow(name: String): Flow<Int> {
        return supabase.from(table = "trader")
            .selectSingleValueAsFlow(primaryKey = TraderRow::name) {
                TraderRow::name eq name
            }.map { it.balance }
    }

    fun getTradesFlow(): Flow<List<TradeRow>> {
        return supabase
            .from("trade")
            .selectAsFlow(primaryKey = TradeRow::id)
    }

    fun getTradesFlow(name: String): Flow<List<TradeRow>> {
        return supabase
            .from("trade")
            .selectAsFlow(
                primaryKey = TradeRow::id,
                filter = FilterOperation("trader", FilterOperator.EQ, name)
            )
    }

    suspend fun updateNews(newsTitles: List<String>) {
        supabase.from(table = "news")
            .delete {
                filter { NewsRow::id gte 0 }
            }
        val rows = newsTitles.mapIndexed { i, title ->
            NewsRow(
                id = i,
                title = title,
            )
        }
        supabase.from(table = "news").insert(values = rows)
    }

    fun getNewsFlow(): Flow<List<NewsRow>> {
        return supabase
            .from("news")
            .selectAsFlow(NewsRow::id)
            .debounce(100)
    }

    fun getsStocksFlow(): Flow<List<StockRow>> {
        return supabase
            .from("stock")
            .selectAsFlow(StockRow::name)
    }

    suspend fun deleteTradingAnalytics() {
        supabase.from(table = "analytics")
            .delete {
                filter { TradingAnalyticsRow::day gte 0 }
            }
    }

    suspend fun saveTradingAnalytics(tradingAnalytics: List<TradingAnalyticsRow>) {
        supabase.from(table = "analytics")
            .insert(values = tradingAnalytics)
    }

    fun getTradingAnalyticsFlow(): Flow<List<TradingAnalyticsRow>> {
        return supabase
            .from("analytics")
            .selectAsFlow(TradingAnalyticsRow::id)
    }

    suspend fun createTrade(
        traderName: String,
        stockName: String,
        buy: Boolean,
    ) {
        val stock = getStockByName(stockName = stockName) ?: return
        supabase.from(table = "trade")
            .insert(
                value = TradeRow(
                    trader = traderName,
                    stock = stock.name,
                    price = if (buy) stock.priceBuy else stock.priceSell,
                    buy = buy,
                )
            )

        val priceChange = if (buy) 10 else -10
        supabase.from(table = "stock")
            .update(
                update = {
                    StockRow::priceBuy setTo stock.priceBuy + priceChange
                    StockRow::priceSell setTo stock.priceSell + priceChange
                }
            ) {
                filter { StockRow::name eq stock.name }
            }
    }

    private suspend fun getStockByName(stockName: String): StockRow? {
        return supabase
            .from("stock")
            .select {
                filter { StockRow::name eq stockName }
            }.decodeSingleOrNull<StockRow>()
    }

}