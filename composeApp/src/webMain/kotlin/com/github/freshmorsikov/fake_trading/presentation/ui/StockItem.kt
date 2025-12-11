package com.github.freshmorsikov.fake_trading.presentation.ui

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.freshmorsikov.fake_trading.core.ui.LocalCompact
import com.github.freshmorsikov.fake_trading.presentation.model.Stock

@Composable
fun StockItem(
    stock: Stock,
    onBuy: () -> Unit,
    onSell: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val compact = LocalCompact.current
    if (compact) {
        Column(
            modifier = modifier,
            verticalArrangement = spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = spacedBy(4.dp),
            ) {
                StockTitle(
                    modifier = Modifier.weight(1f),
                    stockName = stock.name,
                    stockCount = stock.count,
                )
                StockButtons(
                    priceBuy = stock.priceBuy,
                    isBuyEnabled = stock.isBuyEnabled,
                    priceSell = stock.priceSell,
                    isSellEnabled = stock.isSellEnabled,
                    onBuy = onBuy,
                    onSell = onSell,
                )
            }
            StockDescription(stockDescription = stock.description)
            // TODO add analytics
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(4.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = spacedBy(4.dp),
            ) {
                StockTitle(
                    stockName = stock.name,
                    stockCount = stock.count,
                )
                StockDescription(stockDescription = stock.description)
            }
            StockButtons(
                priceBuy = stock.priceBuy,
                isBuyEnabled = stock.isBuyEnabled,
                priceSell = stock.priceSell,
                isSellEnabled = stock.isSellEnabled,
                onBuy = onBuy,
                onSell = onSell,
            )
            // TODO add analytics
        }
    }
}

@Composable
private fun StockTitle(
    stockName: String,
    stockCount: Int,
    modifier: Modifier = Modifier,
) {
    val text = buildAnnotatedString {
        append(stockName)
        if (stockCount > 0) {
            withStyle(style = SpanStyle(color = Color(0xFF27AE60))) {
                append(" × $stockCount")
            }
        }
    }
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun StockButtons(
    priceBuy: Int,
    isBuyEnabled: Boolean,
    priceSell: Int,
    isSellEnabled: Boolean,
    onBuy: () -> Unit,
    onSell: () -> Unit,
) {
    Row(horizontalArrangement = spacedBy(4.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = priceBuy.toString(),
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(
                onClick = {
                    onBuy()
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF27AE60),
                ),
                enabled = isBuyEnabled,
            ) {
                Text(text = "Купить")
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = priceSell.toString(),
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(
                onClick = {
                    onSell()
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFFEB5757),
                ),
                enabled = isSellEnabled,
            ) {
                Text(text = "Продать")
            }
        }
    }
}

@Composable
private fun StockDescription(stockDescription: String) {
    Text(
        text = stockDescription,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.secondary,
    )
}