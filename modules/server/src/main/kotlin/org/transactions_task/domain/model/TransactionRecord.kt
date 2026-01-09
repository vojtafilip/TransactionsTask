package org.transactions_task.domain.model

import kotlin.time.Instant

enum class Currency {
    CZK
}

data class TransactionRecord(
    val reference: Long,
    val timestamp: Instant,
    val amount: Long,
    val currency: Currency,
    val description: String?
)
