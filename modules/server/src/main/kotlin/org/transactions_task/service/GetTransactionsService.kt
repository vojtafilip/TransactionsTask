package org.transactions_task.service

import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord
import org.transactions_task.repository.TransactionsRepository

class GetTransactionsService(
    private val transactionsRepository: TransactionsRepository
) {

    data class GetTransactionsResult(
        val sortedTransactions: List<TransactionRecord>,
        val markedTransactions: List<Reference>,
    )

    fun getTransactions(): GetTransactionsResult {
        val sortedTransactionsResult = transactionsRepository.getSortedTransactions()
        val markedTransactions = sortedTransactionsResult.sortedTransactions
            .filter { it.amount == sortedTransactionsResult.maxAmount }
            .map { it.reference }

        return GetTransactionsResult(
            sortedTransactionsResult.sortedTransactions,
            markedTransactions
        )
    }
}
