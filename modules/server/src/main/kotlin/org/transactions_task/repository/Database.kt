package org.transactions_task.repository

import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureDatabase() {
    // TODO configure from env
    Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL", // MODE=MYSQL to allow insertIgnore in exposed
        user = "root",
        password = ""
    )

    // TODO do it only explicitly at release
    transaction {
        SchemaUtils.create(TransactionsTable)
    }
}
