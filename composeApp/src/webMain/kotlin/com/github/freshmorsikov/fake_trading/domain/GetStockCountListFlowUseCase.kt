package com.github.freshmorsikov.fake_trading.domain

import com.github.freshmorsikov.fake_trading.api.SupabaseApi
import com.github.freshmorsikov.fake_trading.api.model.TradeRow
import com.github.freshmorsikov.fake_trading.data.StockRepository
import com.github.freshmorsikov.fake_trading.domain.model.Stock
import com.github.freshmorsikov.fake_trading.domain.model.TraderName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

data class StockCount(
    val stock: Stock,
    val count: Int,
)

class GetStockCountListFlowUseCase() {

    private val stockRepository = StockRepository()
    private val supabaseApi = SupabaseApi()

    operator fun invoke(traderName: TraderName): Flow<List<StockCount>> {
        val tradesFlow = if (traderName.isAdmin) {
            flowOf(emptyList())
        } else {
            supabaseApi.getTradesFlow(traderName = traderName.name)
        }
        return combine(
            stockRepository.getStocksFlow(),
            tradesFlow,
        ) { stocks, trades ->
            stocks.map { stock ->
                val filtered = trades.filter { trade ->
                    trade.stock == stock.name
                }
                StockCount(
                    stock = stock,
                    count = filtered.count()
                )
            }
        }
    }

    private fun List<TradeRow>.count(): Int {
        val boughtCount = count { trade ->
            trade.buy
        }
        val soldCount = count { trade ->
            !trade.buy
        }

        return boughtCount - soldCount
    }

}