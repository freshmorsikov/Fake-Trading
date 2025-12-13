package com.github.freshmorsikov.fake_trading.domain

import com.github.freshmorsikov.fake_trading.api.SupabaseApi
import com.github.freshmorsikov.fake_trading.api.model.TradeRow
import com.github.freshmorsikov.fake_trading.data.StockRepository
import com.github.freshmorsikov.fake_trading.domain.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class StockCount(
    val stock: Stock,
    val count: Int,
)

class GetStockCountListFlowUseCase() {

    private val stockRepository = StockRepository()
    private val supabaseApi = SupabaseApi()

    operator fun invoke(traderName: String): Flow<List<StockCount>> {
        return combine(
            stockRepository.getStocksFlow(),
            supabaseApi.getTradesFlow(traderName = traderName),
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