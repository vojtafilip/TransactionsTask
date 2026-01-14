package org.transactions_task.screen.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import org.transactions_task.model.Transaction
import org.transactions_task.repository.RepositoryLoadingState
import org.transactions_task.repository.TransactionsRepository

data class TransactionVM(
    val timestamp: String,
    val amount: String,
    val description: String,
    val isBiggest: Boolean
)

sealed class UiState {
    data object Loading : UiState()
    data class Error(val message: String) : UiState()
    data class Success(
        val transactions: List<TransactionVM>,
        val showCursorMessage: Boolean
    ) : UiState()
}

class TransactionsViewModel(
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var loadingJob: Job? = null

    fun loadData() {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            transactionsRepository
                .loadTransactions(viewModelScope)
                .collect { result ->
                    _uiState.emit(result.toUiState())
                }
        }
    }
}

private fun RepositoryLoadingState.toUiState() = when (this) {
    is RepositoryLoadingState.Loading -> UiState.Loading
    is RepositoryLoadingState.Error -> UiState.Error(message)
    is RepositoryLoadingState.Success -> UiState.Success(
        transactions = transactions.map { it.toVM() },
        showCursorMessage = nextCursor != null
    )
}

private fun Transaction.toVM() = TransactionVM(
    timestamp = timestamp.format(DateTimeComponents.Formats.RFC_1123), // TODO nicer format
    amount = amount.toString(),
    description = description,
    isBiggest = isBiggest
)
