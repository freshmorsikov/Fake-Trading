package com.github.freshmorsikov.fake_trading.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.freshmorsikov.fake_trading.presentation.MarketViewModel
import com.github.freshmorsikov.fake_trading.presentation.model.CURRENCY
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
                TopInfoCard(
                    modifier = Modifier.fillMaxWidth(),
                    day = stateValue.day,
                    dayCount = stateValue.dayCount,
                    dayTime = stateValue.dayTime,
                    isAdmin = stateValue.isAdmin
                )

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

@Composable
private fun TopInfoCard(
    day: Int,
    dayCount: Int,
    dayTime: DayTime,
    isAdmin: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DayTime(
            modifier = Modifier.fillMaxWidth(),
            day = day,
            dayCount = dayCount,
            dayTime = dayTime,
        )
        if (!isAdmin) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp)
            )
            Balance(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                cash = 100,
                stockValue = 200,
                total = 300,
            )
        }
    }
}

@Composable
private fun DayTime(
    day: Int,
    dayCount: Int,
    dayTime: DayTime,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val dayTimeText = when (dayTime) {
            DayTime.Morning -> "Утро"
            DayTime.Noon -> "Работа"
            DayTime.Evening -> "Вечер"
        }
        val dayText = buildAnnotatedString {
            append("День ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(day.toString())
            }
            append(" ($dayTimeText)")
        }
        Text(
            text = dayText,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )

        val action = when (dayTime) {
            DayTime.Morning -> "Читаем новости"
            DayTime.Noon -> "Торгуем стонксами"
            DayTime.Evening -> "Подводим итоги"
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = action,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        LinearProgressIndicator(
            modifier = Modifier
                .padding(top = 8.dp)
                .height(height = 8.dp)
                .fillMaxWidth(),
            progress = { day.toFloat() / dayCount },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            gapSize = (-8).dp,
            drawStopIndicator = {},
        )
    }
}

@Composable
private fun Balance(
    cash: Int,
    stockValue: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.sizeIn(maxWidth = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            Text(
                modifier = Modifier
                    .alignByBaseline()
                    .weight(1f),
                text = "Кэш",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                modifier = Modifier.alignByBaseline(),
                text = "$cash $CURRENCY",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Row {
            Text(
                modifier = Modifier
                    .alignByBaseline()
                    .weight(1f),
                text = "Стонксы",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                modifier = Modifier.alignByBaseline(),
                text = "$stockValue $CURRENCY",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Row {
            Text(
                modifier = Modifier
                    .alignByBaseline()
                    .weight(1f),
                text = "Всего",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                modifier = Modifier.alignByBaseline(),
                text = "$total $CURRENCY",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}