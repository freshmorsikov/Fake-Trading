package com.github.freshmorsikov.fake_trading.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.freshmorsikov.fake_trading.presentation.MarketViewModel
import com.github.freshmorsikov.fake_trading.presentation.model.DayTime
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
                                StockItem(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    stock = stock,
                                    onBuy = {
                                        viewModel.buyStock(stockName = stock.name)
                                    },
                                    onSell = {
                                        viewModel.sellStock(stockName = stock.name)
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