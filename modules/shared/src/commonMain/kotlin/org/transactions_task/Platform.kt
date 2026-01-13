package org.transactions_task

// TODO keeping here for future use... or remove

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform