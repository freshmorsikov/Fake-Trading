package com.github.freshmorsikov.fake_trading

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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val INITIAL_BALANCE = 1000

@Serializable
data class StepRow(
    val id: Int,
    val step: Int,
)

@Serializable
data class NewsRow(
    val id: Int,
    val title: String,
)

@Serializable
data class TraderRow(
    val name: String,
    val balance: Int,
)

@Serializable
data class StockRow(
    val name: String,
    val description: String,
    @SerialName("price_buy") val priceBuy: Int,
    @SerialName("price_sell") val priceSell: Int,
)

@Serializable
data class TradeRow(
    val id: Int? = null,
    val trader: String,
    val stock: String,
    val price: Int,
    val buy: Boolean,
)

@Serializable
data class TradingAnalyticsRow(
    val id: Int? = null,
    val stock: String,
    val change: Int,
    val note: String,
    val day: Int,
)

@OptIn(SupabaseExperimental::class, FlowPreview::class)
class SupabaseApi {

    private val supabaseUrl = "https://zqlqyyjovbejoznhlbva.supabase.co"
    private val supabaseKey =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpxbHF5eWpvdmJlam96bmhsYnZhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM5MjEyNzQsImV4cCI6MjA3OTQ5NzI3NH0.pQRHT4qEf96vL-67ByfOSrCqH8hXwF8OOeqed6gt7dY"

    private val supabase = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
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