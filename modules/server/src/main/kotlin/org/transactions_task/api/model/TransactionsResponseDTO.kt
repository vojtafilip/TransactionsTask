package org.transactions_task.api.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionsResponseDTO(
//    val account: Long // TODO some additional info?
    val transactions: List<TransactionDTO>
) {
    @Serializable
    data class TransactionDTO(
        val reference: Long,
        val timestamp: String,
        val amount: Long,
        val currency: String,
        val description: String?
    )
}
