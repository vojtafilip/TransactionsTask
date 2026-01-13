package org.transactions_task.repository.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp
import org.transactions_task.domain.model.Currency

object TransactionsTable: IntIdTable("transactions") {
    val reference = long("reference").uniqueIndex()
    val timestamp = timestamp("timestamp").index()
    val amount = long("amount").index()
    val currency = enumerationByName("currency", 10, Currency::class)
    val description = varchar("description", 1024).nullable() // TODO check length at validation (add a const for length)

    val dbCreated = timestamp("db_created").defaultExpression(CurrentTimestamp)
    // TODO other technical fields
}