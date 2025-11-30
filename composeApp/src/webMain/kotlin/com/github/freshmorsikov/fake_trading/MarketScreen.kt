package com.github.freshmorsikov.fake_trading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MarketScreen(viewModel: MarketViewModel = viewModel { MarketViewModel() }) {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            val state by viewModel.state.collectAsState()
            val stateValue = state

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "День ${stateValue.day}/${stateValue.dayCount}")
                Text(
                    text = when (stateValue.dayTime) {
                        DayTime.Morning -> "Утро: Читаем новости"
                        DayTime.Noon -> "Работа: Торгуем стонксами"
                        DayTime.Evening -> "Вечер: Подводим итоги дня"
                    }
                )

                if (!stateValue.isAdmin) {
                    Text(text = "Баланс: ${stateValue.balance}")
                }

                if (stateValue.name.isEmpty()) {
                    NameInput(
                        modifier = Modifier.padding(top = 16.dp),
                        onSave = { name ->
                            viewModel.setName(name = name)
                        }
                    )
                }

                Row(modifier = Modifier.padding(top = 16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (stateValue.currentNews.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(bottom = 4.dp),
                                text = "Стонксы",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                        stateValue.stocks.forEach { stock ->
                            Column {
                                StockCard(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    stock = stock,
                                    onBuy = {
                                        viewModel.buyStock(stockName = it.name)
                                    },
                                    onSell = {
                                        viewModel.sellStock(stockName = it.name)
                                    },
                                )
                                HorizontalDivider()
                            }
                        }
                    }

                    if (stateValue.isAdmin) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            if (stateValue.currentNews.isNotEmpty()) {
                                Text(
                                    modifier = Modifier.padding(bottom = 4.dp),
                                    text = "Новости",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }
                            stateValue.currentNews.forEach { news ->
                                Text(text = news.title)
                            }

                            if (stateValue.traders.isNotEmpty()) {
                                Text(
                                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                                    text = "Трейдеры",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }
                            stateValue.traders.forEach { trader ->
                                Text(text = "${trader.name}: ${trader.balance}")
                            }
                        }
                    }
                }

                if (stateValue.isAdmin) {
                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Button(
                            onClick = { viewModel.goToPreviousStep() },
                            enabled = stateValue.isPreviousStepAvailable,
                        ) {
                            Text(text = "Назад")
                        }
                        Button(
                            onClick = { viewModel.goToNextStep() },
                            enabled = stateValue.isNextStepAvailable,
                        ) {
                            Text(text = "Продолжить")
                        }
                    }
                    if (stateValue.stepNumber == 0) {
                        Button(
                            modifier = Modifier.padding(top = 16.dp),
                            onClick = { viewModel.generateNews() },
                            enabled = stateValue.isRefreshEnabled,
                        ) {
                            Text(text = "Новости")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StockCard(
    stock: Stock,
    onBuy: (Stock) -> Unit,
    onSell: (Stock) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stock.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stock.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            stock.analytics?.let { analytics ->
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiary.copy(
                                alpha = 0.2f
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp),
                ) {
                    val color = if (analytics.change > 0) {
                        Color(0xFF27AE60)
                    } else {
                        Color(0xFFEB5757)
                    }
                    val sign = if (analytics.change > 0) {
                        "+"
                    } else {
                        ""
                    }
                    Text(
                        text = "$sign${analytics.change}",
                        color = color,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = analytics.note,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        if (stock.count > 0) {
            Text(
                text = "x ${stock.count}",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stock.priceBuy.toString(),
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(
                onClick = {
                    onBuy(stock)
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF27AE60),
                ),
                enabled = stock.isBuyEnabled,
            ) {
                Text(text = "Купить")
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stock.priceSell.toString(),
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(
                onClick = {
                    onSell(stock)
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFFEB5757),
                ),
                enabled = stock.isSellEnabled,
            ) {
                Text(text = "Продать")
            }
        }
    }
}

@Composable
private fun NameInput(
    modifier: Modifier = Modifier,
    onSave: (String) -> Unit,
) {
    var input by remember {
        mutableStateOf("")
    }
    Column(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = input,
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Имя"
                )
            },
            onValueChange = { value ->
                input = value
            },
        )
        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = {
                onSave(input)
            },
        ) {
            Text(text = "Начать")
        }
    }
}