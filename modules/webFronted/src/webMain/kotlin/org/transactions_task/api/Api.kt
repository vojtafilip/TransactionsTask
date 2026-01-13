package org.transactions_task.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.transactions_task.api.dto.TransactionsDTO
import org.transactions_task.api.dto.TransactionsDTO.TransactionDTO
import org.transactions_task.model.Transaction
import kotlin.time.Instant

interface Api {

    sealed class LoadResponse {
        data class Success(val transactions: List<Transaction>) : LoadResponse()
        data class Error(val message: String) : LoadResponse()
    }

    suspend fun loadTransactions(): LoadResponse
}


// TODO to configuration
const val BACKEND_URL = "http://localhost:5000/transactions?format=json"

class HttpApi : Api {

    private val httpClient = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000
            connectTimeoutMillis = 5_000
            socketTimeoutMillis  = 10_000
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    override suspend fun loadTransactions(): Api.LoadResponse {

        try {
            val transactionsDTO: TransactionsDTO = httpClient.get(BACKEND_URL).body()

            return Api.LoadResponse.Success(
                transactions = transactionsDTO.sortedTransactions.map {
                    it.convertToTransaction()
                }
            )

        } catch (e: Exception) {
            return Api.LoadResponse.Error(e.message ?: "Unknown error")
        } catch (t: Throwable) {
            // TODO kotlin.Error is caught (with JsError as cause) ... use better engine to catch correct errors.
            // TODO this is very technical, change to user friendly message
            return Api.LoadResponse.Error("${t::class.qualifiedName} - ${t.message}")
        }

        /* TODO more catches:
        } catch (e: ClientRequestException) {
            // 4xx
        } catch (e: ServerResponseException) {
            // 5xx
        } catch (e: HttpRequestTimeoutException) {
            // timeout
        } catch (e: IOException) {
            // network error
        } catch (e: SerializationException) {
            // JSON error
        }
         */
    }
}

private fun TransactionDTO.convertToTransaction(): Transaction = Transaction(
    timestamp = Instant.parse(timestamp), // TODO error handling
    amount = amount,
    description = description,
    isBiggest = isBiggest
)
