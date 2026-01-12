package org.transactions_task.api

import kotlinx.coroutines.delay
import org.transactions_task.model.Transaction

interface Api {

    sealed class LoadResponse {
        data class Success(val transactions: List<Transaction>) : LoadResponse()
        data class Error(val message: String) : LoadResponse()
    }

    suspend fun loadTransactions(): LoadResponse
}

class HttpApi : Api {

    override suspend fun loadTransactions(): Api.LoadResponse {

        // TODO("Not yet implemented")
        delay(1000) // TODO remove

        return Api.LoadResponse.Error("Not implemented")
    }
}
