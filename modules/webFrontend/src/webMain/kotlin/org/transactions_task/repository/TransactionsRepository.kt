package org.transactions_task.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.transactions_task.api.Api
import org.transactions_task.model.Transaction

sealed class RepositoryLoadingState {
    data object Loading : RepositoryLoadingState()
    data class Error(val message: String) : RepositoryLoadingState()
    data class Success(val transactions: List<Transaction>) : RepositoryLoadingState()
}

class TransactionsRepository(
    private val api: Api
) {
    suspend fun loadTransactions(
        scope: CoroutineScope
    ): StateFlow<RepositoryLoadingState> = flow {
        emit(RepositoryLoadingState.Loading)

        val loadingState = when (
            val result = api.loadTransactions()
        ) {
            is Api.LoadResponse.Error -> RepositoryLoadingState.Error(result.message)
            is Api.LoadResponse.Success -> RepositoryLoadingState.Success(result.transactions)
        }

        emit(loadingState)
    }.stateIn(scope)
}
