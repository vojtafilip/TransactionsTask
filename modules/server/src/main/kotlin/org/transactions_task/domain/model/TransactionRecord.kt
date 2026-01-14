package org.transactions_task.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
@JvmInline
value class Reference(val ref: Long)

// Now we support only required currencies. TODO make it configurable
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
