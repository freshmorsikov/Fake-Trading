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
import com.github.freshmorsikov.fake_trading.presentation.model.MarketState
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

            Box(modifier = Modifier.padding(16.dp)) {
                if (stateValue.isInit) {
                    NameInput(
                        onSave = { name ->
                            viewModel.setName(name = name)
                        }
                    )
                } else {
                    TradingContent(
                        marketState = stateValue,
                        onBuyClick = { viewModel.buyStock(stockName = it) },
                        onSellClick = { viewModel.sellStock(stockName = it) },
                        onPreviousClick = { viewModel.goToPreviousStep() },
                        onNextClick = { viewModel.goToNextStep() },
                        onNewsClick = { viewModel.generateNews() },
                    )
                }
            }
        }
    }
}

@Composable
private fun TradingContent(
    marketState: MarketState,
    onBuyClick: (String) -> Unit,
    onSellClick: (String) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onNewsClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopInfoCard(
            modifier = Modifier.fillMaxWidth(),
            day = marketState.day,
            dayTime = marketState.dayTime,
            progress = marketState.progress,
            isAdmin = marketState.isAdmin
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                if (marketState.currentNews.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        text = "Стонксы",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                marketState.stocks.forEach { stock ->
                    Column {
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

            if (marketState.isAdmin) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    if (marketState.currentNews.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(bottom = 4.dp),
                            text = "Новости",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    marketState.currentNews.forEach { news ->
                        Text(text = news.title)
                    }

                    if (marketState.traders.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                            text = "Трейдеры",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    marketState.traders.forEach { trader ->
                        Text(text = "${trader.name}: ${trader.balance}")
                    }
                }
            }
        }

        if (marketState.isAdmin) {
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = { onPreviousClick() },
                    enabled = marketState.isPreviousStepAvailable,
                ) {
                    Text(text = "Назад")
                }
                Button(
                    onClick = { onNextClick() },
                    enabled = marketState.isNextStepAvailable,
                ) {
                    Text(text = "Продолжить")
                }
            }
            if (marketState.stepNumber == 0) {
                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = { onNewsClick() },
                    enabled = marketState.isRefreshEnabled,
                ) {
                    Text(text = "Новости")
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
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                onClick = {
                    onSave(input)
                },
            ) {
                Text(text = "Начать")
            }
        }
    }
}

@Composable
private fun TopInfoCard(
    day: Int,
    dayTime: DayTime,
    progress: Float,
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
            dayTime = dayTime,
            progress = progress,
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
    dayTime: DayTime,
    progress: Float,
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
            progress = { progress },
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