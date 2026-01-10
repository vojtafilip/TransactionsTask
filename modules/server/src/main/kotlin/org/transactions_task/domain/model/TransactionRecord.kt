package org.transactions_task.domain.model

import kotlin.time.Instant

@JvmInline
value class Reference(val ref: Long)

enum class Currency {
    CZK
}

data class TransactionRecord(
    val reference: Reference,
    val timestamp: Instant,
    val amount: Long,
    val currency: Currency,
    val description: String?
)
