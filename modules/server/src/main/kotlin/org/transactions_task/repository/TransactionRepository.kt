package org.transactions_task.repository

import org.transactions_task.domain.model.TransactionRecord

interface TransactionsRepository {

    data class InsertResult(
        val insertedCount: Int,
        val failedTransactions: List<TransactionRecord>
    )

    fun insertTransactions(transactions: List<TransactionRecord>): InsertResult

    fun getSortedTransactions(): List<TransactionRecord>
}

