package org.transactions_task

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform