package org.transactions_task.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.max
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord
import org.transactions_task.repository.TransactionsRepository.GetSortedTransactionsResult
import org.transactions_task.repository.TransactionsRepository.InsertResult

class ExposedTransactionsRepository : TransactionsRepository {

    override fun insertTransactions(transactions: List<TransactionRecord>): InsertResult {

        var insertedCount = 0
        val failedToInsert = mutableListOf<Reference>()

        // TODO for each record, change to suspendTransaction
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

    override fun getSortedTransactions(): GetSortedTransactionsResult = transaction {
        val sortedTransactions = getSortedTransactionsInternal()
        val maxAmount = getMaxAmount()

        GetSortedTransactionsResult(sortedTransactions, maxAmount)
    }

    private fun getSortedTransactionsInternal(): List<TransactionRecord> =
        TransactionsTable
            .selectAll()
            .orderBy(TransactionsTable.timestamp, SortOrder.DESC)
            //            .limit(1000) TODO set limit, add pagination
            .map { it.toTransactionRecord() }

    private fun getMaxAmount(): Long? {
        val maxAmountColumn = TransactionsTable.amount.max()
        return TransactionsTable
            .select(maxAmountColumn)
            .firstOrNull()
            ?.get(maxAmountColumn)
    }

    private fun ResultRow.toTransactionRecord(): TransactionRecord = TransactionRecord(
        reference = Reference(this[TransactionsTable.reference]),
        timestamp = this[TransactionsTable.timestamp],
        amount = this[TransactionsTable.amount],
        currency = this[TransactionsTable.currency],
        description = this[TransactionsTable.description]
    )
}
