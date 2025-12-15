package com.github.freshmorsikov.fake_trading.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
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
import com.github.freshmorsikov.fake_trading.domain.model.Step
import com.github.freshmorsikov.fake_trading.presentation.model.BalanceUi
import com.github.freshmorsikov.fake_trading.presentation.model.CURRENCY
import com.github.freshmorsikov.fake_trading.presentation.model.DayTime

@Composable
fun TopInfoCard(
    step: Step,
    balance: BalanceUi?,
    isAdmin: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        if (isAdmin) {
            Spacer(modifier = Modifier.weight(1f))
        }
        Column(
            modifier = Modifier
                .weight(1f)
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
                step = step,
            )
            balance?.let {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 16.dp)
                )
                Balance(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    balance = balance
                )
            }
        }
        if (isAdmin) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun DayTime(
    step: Step,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val dayTimeText = when (step.dayTime) {
            DayTime.Morning -> "Утро"
            DayTime.Noon -> "Работа"
            DayTime.Evening -> "Вечер"
        }
        val dayText = buildAnnotatedString {
            append("День ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(step.day.toString())
            }
            append(" ($dayTimeText)")
        }
        Text(
            text = dayText,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )

        val action = when (step.dayTime) {
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
            progress = { step.progress },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            gapSize = (-8).dp,
            drawStopIndicator = {},
        )
    }
}

@Composable
private fun Balance(
    balance: BalanceUi,
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
                text = "${balance.cash} $CURRENCY",
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
                text = "${balance.stocks} $CURRENCY",
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
                text = "${balance.total} $CURRENCY",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}