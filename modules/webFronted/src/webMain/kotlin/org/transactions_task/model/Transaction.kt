package org.transactions_task.model

data class Transaction(
    val timestamp: String,
    val amount: Long,
    val description: String,
    val isBiggest: Boolean
)
