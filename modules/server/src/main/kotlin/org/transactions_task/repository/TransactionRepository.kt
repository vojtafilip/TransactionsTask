package org.transactions_task.repository

import kotlinx.serialization.Serializable
import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord
import kotlin.time.Instant

interface TransactionsRepository {

    fun insertTransaction(transaction: TransactionRecord): Boolean

    data class GetSortedTransactionsResult(
        val sortedTransactions: List<TransactionRecord>,
        val maxAmount: Long?, // null if no transactions were found
        val cursor: Cursor? // null if there are no more transactions
    )

    @Serializable
    data class Cursor(
        val timestamp: Instant,
        val reference: Reference
    )

    fun getSortedTransactions(cursor: Cursor?, limit: Int): GetSortedTransactionsResult
}
