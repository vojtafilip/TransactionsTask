package org.transactions_task.screen.transactions.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.transactions_task.AppConfig.TRANSACTIONS_LIMIT
import org.transactions_task.screen.transactions.TransactionVM

@Composable
fun TransactionsList(
    modifier: Modifier = Modifier.Companion,
    transactions: List<TransactionVM>,
    showCursorMessage: Boolean
) {
    Column(
        modifier = modifier.testTag("transactionsList"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("All transactions:")

        ListHeader()
        LazyColumn {
            // TODO scrollbar
            items(transactions) { transaction ->
                ListRow(transaction)
            }
        }

        if (showCursorMessage) {
            // TODO implement pagination
            Text("Pagination is not implemented yet. Showing just first $TRANSACTIONS_LIMIT transactions.")
        }
    }
}

@Composable
private fun ListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Timestamp",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(2f)
                .padding(10.dp)
        )
        Text(
            text = "Amount",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        )
        Text(
            text = "Description",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(3f)
                .padding(10.dp)
        )
    }
}

@Composable
private fun ListRow(transaction: TransactionVM) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .background(if (transaction.isBiggest) Color.Green else Color.White)
    ) {
        if (transaction.isBiggest) {
            Text(
                text = "X",
                modifier = Modifier
                    .width(16.dp)
                    .padding(vertical = 10.dp)
                    .testTag("biggestTransaction - ${transaction.amount}")
            )
        } else {
            Text(
                text = " ",
                modifier = Modifier
                    .width(16.dp)
                    .padding(vertical = 10.dp)
            )
        }
        Text(
            text = transaction.timestamp,
            modifier = Modifier
                .weight(2f)
                .padding(2.dp)
                .border(1.dp, Color.Black)
                .padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis

        )
        Text(
            text = transaction.amount,
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .border(1.dp, Color.Black)
                .padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = transaction.description,
            modifier = Modifier
                .weight(3f)
                .padding(2.dp)
                .border(1.dp, Color.Black)
                .padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
