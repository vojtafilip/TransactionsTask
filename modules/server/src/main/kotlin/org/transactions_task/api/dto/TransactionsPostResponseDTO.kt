package org.transactions_task.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionsPostResponseDTO(
    val insertedCount: Int,
    val failedToInsert: List<Long>
)
