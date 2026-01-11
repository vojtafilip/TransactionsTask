package org.transactions_task

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.transactions_task.api.configureRouting
import org.transactions_task.api.configureSerialization
import org.transactions_task.di.configureKoin
import org.transactions_task.repository.configureDatabase


fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module(testing: Boolean = false) {
    configureKoin()
    configureDatabase(dropTables = testing)
    configureSerialization()
    configureRouting()
}
