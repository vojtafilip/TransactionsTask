package org.transactions_task.repository

import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord

interface TransactionsRepository {

    data class InsertResult(
        val insertedCount: Int,
        val failedToInsert: List<Reference>
    )

    fun insertTransactions(transactions: List<TransactionRecord>): InsertResult

    fun getSortedTransactions(): List<TransactionRecord>
}

