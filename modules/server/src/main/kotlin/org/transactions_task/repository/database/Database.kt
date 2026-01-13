package org.transactions_task.repository.database

import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureDatabase(dropTables: Boolean = false) {
    // TODO configure from env
    Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL", // MODE=MYSQL to allow insertIgnore in exposed
        user = "root",
        password = ""
    )

    val tables = arrayOf(TransactionsTable)

    // TODO do it only explicitly at release
    transaction {
        if (dropTables) {
            SchemaUtils.drop(*tables)
        }
        SchemaUtils.create(*tables)
    }
}
