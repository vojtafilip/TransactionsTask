package org.transactions_task.repository

import org.transactions_task.domain.model.TransactionRecord

class ExposedTransactionsRepository: TransactionsRepository {
    override fun insertTransactions(transactions: List<TransactionRecord>): TransactionsRepository.InsertResult {
        return TransactionsRepository.InsertResult(0, emptyList())
        TODO("Not yet implemented")
    }

    override fun getSortedTransactions(): List<TransactionRecord> {
        TODO("Not yet implemented")
    }
}