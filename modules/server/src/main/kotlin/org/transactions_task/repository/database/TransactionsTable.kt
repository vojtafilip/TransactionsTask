package org.transactions_task.repository.database

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp
import org.transactions_task.Config.TRANSACTION_DESCRIPTION_MAX_LENGTH
import org.transactions_task.domain.model.Currency

object TransactionsTable : IntIdTable("transactions") {
    val reference = long("reference").uniqueIndex()
    val timestamp = timestamp("timestamp")
    val amount = long("amount").index()
    val currency = enumerationByName("currency", 3, Currency::class)
    val description = varchar("description", TRANSACTION_DESCRIPTION_MAX_LENGTH).nullable()

    val dbCreated = timestamp("db_created").defaultExpression(CurrentTimestamp)
    // TODO other technical fields if needed

    init {
        index(isUnique = false, timestamp, reference)
    }
}
