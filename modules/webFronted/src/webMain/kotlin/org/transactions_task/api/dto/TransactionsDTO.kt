package org.transactions_task.api.dto

import kotlinx.serialization.Serializable

/*
 * This is copy of DTO from backend.
 * TODO generate it in the future from an API description
 */

@Serializable
data class TransactionsDTO(
    val sortedTransactions: List<TransactionDTO>,
//  TODO  val nextCursor: String?
) {
    @Serializable
    data class TransactionDTO(
        val timestamp: String,
        val amount: Long,
        val description: String,
        val isBiggest: Boolean
    )
}
