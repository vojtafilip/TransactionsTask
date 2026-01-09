package org.transactions_task.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionsResponseDTO(
//    val account: Long // TODO some additional info?
    val sortedTransactions: List<TransactionDTO>
) {
    @Serializable
    data class TransactionDTO(
        val timestamp: String,
        val amount: Long,
        val description: String,
        val isBiggest: Boolean
    )
}
