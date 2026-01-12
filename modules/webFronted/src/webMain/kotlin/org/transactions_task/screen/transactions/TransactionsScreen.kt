package org.transactions_task.screen.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.transactions_task.di.viewModel
import org.transactions_task.screen.transactions.view.ErrorIndicator
import org.transactions_task.screen.transactions.view.LoadingIndicator
import org.transactions_task.screen.transactions.view.TransactionsList

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // TODO reload...
        viewModel.loadData()
    }

    TransactionsContent(uiState)
}

@Composable
private fun TransactionsContent(uiState: UiState) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(1000.dp)
            .background(Color.White)
    ) {
        Text(
            text = "Transactions",
            fontSize = 24.sp,
        )
        HorizontalDivider()

        when (uiState) {
            is UiState.Loading -> LoadingIndicator(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(100.dp)
            )

            is UiState.Error -> ErrorIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(100.dp),
                message = uiState.message
            )

            is UiState.Success -> TransactionsList(
                modifier = Modifier.padding(16.dp),
                transactions = uiState.transactions
            )
        }
    }
}
