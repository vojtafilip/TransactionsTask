package org.transactions_task

import io.ktor.server.application.Application
import org.transactions_task.api.configureRouting
import org.transactions_task.api.configureSerialization
import org.transactions_task.di.configureKoin
import org.transactions_task.repository.database.configureDatabase


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

// referenced from configuration
fun Application.module() {
    configureKoin()
    configureDatabase()
    configureSerialization()
    configureRouting()
}
