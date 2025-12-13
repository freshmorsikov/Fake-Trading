package com.github.freshmorsikov.fake_trading.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.freshmorsikov.fake_trading.presentation.MarketViewModel
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
                if (stateValue.traderName.isNone) {
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
            step = marketState.step,
            balance = marketState.balance
        )
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (marketState.traderName.isAdmin) {
                NewsCard(
                    modifier = Modifier.weight(1f),
                    news = marketState.currentNews,
                )
            }
            StocksCard(
                modifier = Modifier.weight(2f),
                stocks = marketState.stocks,
                onBuyClick = onBuyClick,
                onSellClick = onSellClick,
            )
            if (marketState.traderName.isAdmin) {
                TradersCard(
                    modifier = Modifier.weight(1f),
                    traders = marketState.traders,
                )
            }
        }

        if (marketState.traderName.isAdmin) {
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = { onPreviousClick() },
                    enabled = marketState.step.isPreviousStepAvailable,
                ) {
                    Text(text = "Назад")
                }
                Button(
                    onClick = { onNextClick() },
                    enabled = marketState.step.isNextStepAvailable,
                ) {
                    Text(text = "Продолжить")
                }
            }
            if (marketState.step.number == 0) {
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