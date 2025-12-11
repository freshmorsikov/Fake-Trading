package com.github.freshmorsikov.fake_trading.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.freshmorsikov.fake_trading.core.ui.LocalCompact
import com.github.freshmorsikov.fake_trading.core.ui.rememberButtonState
import com.github.freshmorsikov.fake_trading.domain.model.CommonTradingAnalytics
import com.github.freshmorsikov.fake_trading.presentation.model.CURRENCY
import com.github.freshmorsikov.fake_trading.presentation.model.StockUi

private val GreenColor = Color(0xFF27AE60)
private val RedColor = Color(0xFFEB5757)

@Composable
fun StockItem(
    stock: StockUi,
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
            stock.analytics?.let { analytics ->
                StockAnalytics(analytics = analytics)
            }
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
                stock.analytics?.let { analytics ->
                    StockAnalytics(analytics = analytics)
                }
            }
            StockButtons(
                priceBuy = stock.priceBuy,
                isBuyEnabled = stock.isBuyEnabled,
                priceSell = stock.priceSell,
                isSellEnabled = stock.isSellEnabled,
                onBuy = onBuy,
                onSell = onSell,
            )
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
            withStyle(style = SpanStyle(color = GreenColor)) {
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
        val buttonState = rememberButtonState()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$priceBuy $CURRENCY",
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(
                onClick = {
                    buttonState.setClicked()
                    onBuy()
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = GreenColor,
                ),
                enabled = buttonState.isEnabled && isBuyEnabled,
            ) {
                Text(text = "Купить")
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$priceSell $CURRENCY",
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(
                onClick = {
                    buttonState.setClicked()
                    onSell()
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = RedColor,
                ),
                enabled = buttonState.isEnabled && isSellEnabled,
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

@Composable
private fun StockAnalytics(
    analytics: CommonTradingAnalytics,
) {
    val analyticsText = buildAnnotatedString {
        val color = if (analytics.change > 0) {
            GreenColor
        } else {
            RedColor
        }
        val sign = if (analytics.change > 0) { "+" } else { "-" }
        withStyle(
            style = SpanStyle(
                color = color,
                fontWeight = FontWeight.Bold,
            )
        ) {
            append(sign)
            append("${analytics.change}%")
        }
        append(" ${analytics.note}")
    }
    Text(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
            )
            .padding(4.dp),
        text = analyticsText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.secondary,
    )
}