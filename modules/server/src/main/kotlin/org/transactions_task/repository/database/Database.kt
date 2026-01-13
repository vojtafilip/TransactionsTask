package org.transactions_task.repository.database

import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureDatabase() {

    // TODO separate to a configuration logic, to process of application configs at one place
    val dbUrl = environment.config.propertyOrNull("ktor.db.url")!!.getString()
    val dbUser = environment.config.propertyOrNull("ktor.db.user")!!.getString()
    val dbPassword = environment.config.propertyOrNull("ktor.db.password")!!.getString()
    val dropTables = environment.config.propertyOrNull("ktor.db.dropTables")?.getString().toBoolean()

    Database.connect(
        url = dbUrl,
        user = dbUser,
        password = dbPassword
    )

    val tables = arrayOf(TransactionsTable)

    // TODO do it only explicitly at release
    transaction {
        if (dropTables) {
            // For tests, see tests configuration
            SchemaUtils.drop(*tables)
        }
        SchemaUtils.create(*tables)
    }
}
