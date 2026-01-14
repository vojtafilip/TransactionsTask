package org.transactions_task.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionsGetResponseDTO(
    val sortedTransactions: List<TransactionDTO>,
    val nextCursor: String?
) {
    @Serializable
    data class TransactionDTO(
        val timestamp: String,
        val amount: Long,
        val description: String,
        val isBiggest: Boolean
    )
}
