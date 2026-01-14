package org.transactions_task.service

import kotlinx.serialization.json.Json
import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord
import org.transactions_task.repository.TransactionsRepository
import org.transactions_task.repository.TransactionsRepository.Cursor
import kotlin.io.encoding.Base64

class GetTransactionsService(
    private val transactionsRepository: TransactionsRepository
) {

    sealed class GetTransactionsResult {
        data class BadRequest(
            val message: String
        ) : GetTransactionsResult()

        data class Success(
            val sortedTransactions: List<TransactionRecord>,
            val markedTransactions: List<Reference>,
            val nextCursor: String?
        ) : GetTransactionsResult()
    }

    fun getTransactions(cursor: String?, limit: Int): GetTransactionsResult {
        val decodedCursor = try {
            decodeCursor(cursor)
        } catch (_: IllegalArgumentException) {
            return GetTransactionsResult.BadRequest("Invalid cursor.")
        }

        val sortedTransactionsResult = transactionsRepository.getSortedTransactions(
            decodedCursor,
            limit
        )

        val markedTransactions = sortedTransactionsResult.sortedTransactions
            .filter { it.amount == sortedTransactionsResult.maxAmount }
            .map { it.reference }

        return GetTransactionsResult.Success(
            sortedTransactionsResult.sortedTransactions,
            markedTransactions,
            encodeCursor(sortedTransactionsResult.cursor)
        )
    }

    private fun decodeCursor(cursor: String?): Cursor? {
        if (cursor == null) return null

        val json: String = Base64.UrlSafe.decode(cursor).decodeToString()
        val cursor: Cursor = Json.decodeFromString(json)

        return cursor
    }

    private fun encodeCursor(cursor: Cursor?): String? {
        if (cursor == null) return null

        val json: String = Json.encodeToString(cursor)
        val encodedCursor: String = Base64.UrlSafe.encode(json.encodeToByteArray())

        return encodedCursor
    }
}
