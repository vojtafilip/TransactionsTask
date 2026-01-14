package org.transactions_task.repository

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.max
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord
import org.transactions_task.repository.TransactionsRepository.Cursor
import org.transactions_task.repository.TransactionsRepository.GetSortedTransactionsResult
import org.transactions_task.repository.TransactionsRepository.InsertResult
import org.transactions_task.repository.database.TransactionsTable
import kotlin.time.Instant

class ExposedTransactionsRepository : TransactionsRepository {

    override suspend fun insertTransactions(transactions: List<TransactionRecord>): InsertResult {

        var insertedCount = 0
        val failedToInsert = mutableListOf<Reference>()

        transactions.forEach { transaction ->
            val inserted = insertTransaction(transaction)

            if (inserted) {
                insertedCount++
            } else {
                failedToInsert.add(transaction.reference)
            }
        }

        return InsertResult(insertedCount, failedToInsert)
    }

    private suspend fun insertTransaction(transaction: TransactionRecord): Boolean =
        suspendTransaction {
            val count = TransactionsTable.insertIgnore {
                it[reference] = transaction.reference.ref
                it[timestamp] = transaction.timestamp
                it[amount] = transaction.amount
                it[currency] = transaction.currency
                it[description] = transaction.description
            }.insertedCount
            return@suspendTransaction count != 0
        }

    override suspend fun getSortedTransactions(
        cursor: Cursor?,
        limit: Int
    ): GetSortedTransactionsResult =
        suspendTransaction {
            val (sortedTransactions, nextCursor) = getSortedTransactionsInternal(cursor, limit)
            val maxAmount = getMaxAmount()

            GetSortedTransactionsResult(sortedTransactions, maxAmount, nextCursor)
        }

    private fun getSortedTransactionsInternal(
        cursor: Cursor?,
        limit: Int
    ): Pair<List<TransactionRecord>, Cursor?> {
        val timestamp: Instant = cursor?.timestamp ?: Instant.DISTANT_FUTURE
        val reference: Reference = cursor?.reference ?: Reference(Long.MAX_VALUE)

        val transactionRecords = querySortedTransactions(timestamp, reference, limit)

        val nextCursor = if (transactionRecords.size == limit) {
            Cursor(
                transactionRecords.last().timestamp,
                transactionRecords.last().reference
            )
        } else {
            null
        }

        return Pair(transactionRecords, nextCursor)
    }

    private fun querySortedTransactions(
        timestamp: Instant,
        reference: Reference,
        limit: Int
    ): List<TransactionRecord> = TransactionsTable
        .selectAll()
        .where {
            (TransactionsTable.timestamp less timestamp)
                .or(
                    (TransactionsTable.timestamp eq timestamp)
                        .and(TransactionsTable.reference less reference.ref)
                )
        }
        .orderBy(
            TransactionsTable.timestamp to SortOrder.DESC,
            TransactionsTable.reference to SortOrder.DESC
        )
        .limit(limit)
        .map { it.toTransactionRecord() }

    private fun ResultRow.toTransactionRecord(): TransactionRecord = TransactionRecord(
        reference = Reference(this[TransactionsTable.reference]),
        timestamp = this[TransactionsTable.timestamp],
        amount = this[TransactionsTable.amount],
        currency = this[TransactionsTable.currency],
        description = this[TransactionsTable.description]
    )

    private fun getMaxAmount(): Long? {
        val maxAmountColumn = TransactionsTable.amount.max()
        return TransactionsTable
            .select(maxAmountColumn)
            .firstOrNull()
            ?.get(maxAmountColumn)
    }
}
