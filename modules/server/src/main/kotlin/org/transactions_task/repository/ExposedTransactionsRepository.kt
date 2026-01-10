package org.transactions_task.repository

import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord
import org.transactions_task.repository.TransactionsRepository.InsertResult

class ExposedTransactionsRepository : TransactionsRepository {

    override fun insertTransactions(transactions: List<TransactionRecord>): InsertResult {

        var insertedCount = 0
        val failedToInsert = mutableListOf<Reference>()

        // TODO for all or to each record?
        transaction {

            transactions.forEach { transaction ->
                val inserted = insertTransaction(transaction)

                if (inserted) {
                    insertedCount++
                } else {
                    failedToInsert.add(transaction.reference)
                }
            }
        }

        return InsertResult(insertedCount, failedToInsert)
    }

    private fun insertTransaction(transaction: TransactionRecord): Boolean {
        val count = TransactionsTable.insertIgnore {
            it[reference] = transaction.reference.ref
            it[timestamp] = transaction.timestamp
            it[amount] = transaction.amount
            it[currency] = transaction.currency
            it[description] = transaction.description
        }.insertedCount
        return count != 0
    }

    override fun getSortedTransactions(): List<TransactionRecord> {
        TODO("Not yet implemented")
    }
}
