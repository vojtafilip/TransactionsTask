package org.transactions_task.api

import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.html.HTML
import kotlinx.html.TBODY
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.caption
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.title
import kotlinx.html.tr
import org.transactions_task.api.Routes.TRANSACTIONS
import org.transactions_task.api.dto.TransactionsGetResponseDTO
import org.transactions_task.domain.model.TransactionRecord
import org.transactions_task.service.GetTransactionsService.GetTransactionsResult


fun GetTransactionsResult.toDTO() = TransactionsGetResponseDTO(
    sortedTransactions = sortedTransactions.map {
        TransactionsGetResponseDTO.TransactionDTO(
            timestamp = it.timestamp.toString(),
            amount = it.amount,
            description = it.description ?: "",
            isBiggest = it.reference in markedTransactions
        )
    },
    nextCursor = nextCursor
)

fun HTML.transactionsResultToHtml(
    getTransactionsResult: GetTransactionsResult,
    limit: Int
) {
    head {
        title { +"Transactions" }
    }
    body {
        h1 { +"Transactions" }
        table {
            style = "border: 1px solid black;"
            caption { +"List of transactions:" }
            thead {
                tr {
                    th { +"Timestamp" }
                    th { +"Amount" }
                    th { +"Description" }
                }
            }
            tbody {
                getTransactionsResult.sortedTransactions.forEach { transaction ->
                    val isBiggest = transaction.reference in getTransactionsResult.markedTransactions
                    renderTransactionRow(isBiggest, transaction)
                }
            }
        }
        if (getTransactionsResult.nextCursor != null) {
            a(href = createNextPageLink(limit, getTransactionsResult.nextCursor)) {
                +"Show next page"
            }
        }
    }
}

const val TD_STYLE = "border: 1px solid black;padding: 5px"

private fun TBODY.renderTransactionRow(
    isBiggest: Boolean,
    transaction: TransactionRecord
) {
    tr {
        if (isBiggest) {
            style = "background-color: lightgreen;"
        }
        td {
            style = TD_STYLE
            val timestamp = transaction.timestamp
                .format(DateTimeComponents.Formats.RFC_1123) // TODO nicer format
            +timestamp
        }
        td {
            style = TD_STYLE
            +"${transaction.amount}"
        }
        td {
            style = TD_STYLE
            val description = transaction.description ?: ""
            // description length is already limited in DB
            +description
        }
    }
}

private fun createNextPageLink(limit: Int, nextCursor: String) =
    "$TRANSACTIONS?limit=$limit&cursor=${nextCursor}"
