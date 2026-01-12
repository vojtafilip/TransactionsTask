package org.transactions_task.model

import kotlin.time.Instant

data class Transaction(
    val timestamp: Instant,
    val amount: Long,
    val description: String,
    val isBiggest: Boolean
)
