package com.github.freshmorsikov.fake_trading.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.freshmorsikov.fake_trading.presentation.model.CURRENCY
import com.github.freshmorsikov.fake_trading.presentation.model.TraderUi

@Composable
fun TradersCard(
    traders: List<TraderUi>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Трейдеры",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            traders.forEach { trader ->
                Row {
                    Text(
                        modifier = Modifier
                            .alignByBaseline()
                            .weight(1f),
                        text = trader.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = "${trader.balance} $CURRENCY",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}