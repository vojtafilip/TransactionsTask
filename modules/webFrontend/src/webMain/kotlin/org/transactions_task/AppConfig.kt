package org.transactions_task

object AppConfig {
    const val TRANSACTIONS_LIMIT = 500
    // TODO configurable for dev and prod release
    const val BACKEND_URL = "http://localhost:5000/transactions?format=json&limit=$TRANSACTIONS_LIMIT"
}
