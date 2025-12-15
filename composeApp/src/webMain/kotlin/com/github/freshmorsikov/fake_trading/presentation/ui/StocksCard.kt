package com.github.freshmorsikov.fake_trading.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.freshmorsikov.fake_trading.presentation.model.StockUi

@Composable
fun StocksCard(
    stocks: List<StockUi>,
    onBuyClick: (String) -> Unit,
    onSellClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Стонксы",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
        )
        stocks.forEach { stock ->
            StockItem(
                modifier = Modifier.padding(vertical = 8.dp),
                stock = stock,
                onBuy = {
                    onBuyClick(stock.name)
                },
                onSell = {
                    onSellClick(stock.name)
                },
            )
            HorizontalDivider()
        }
    }
}