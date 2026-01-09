package org.transactions_task.service

import org.transactions_task.repository.TransactionsRepository
import org.transactions_task.service.TransactionsCsvReader.CsvReadResult.EmptyCsv
import org.transactions_task.service.TransactionsCsvReader.CsvReadResult.MissingCsvField
import org.transactions_task.service.TransactionsCsvReader.CsvReadResult.Success
import org.transactions_task.service.TransactionsCsvReader.CsvReadResult.WrongCsvHeader
import org.transactions_task.service.TransactionsCsvReader.CsvReadResult.WrongCsvLine
import org.transactions_task.service.PostTransactionsService.ProcessResult.*
import java.io.InputStream

class PostTransactionsService(
    private val transactionsRepository: TransactionsRepository
) {
    private val transactionsCsvReader = TransactionsCsvReader()

    sealed class ProcessResult() {
        data class BadRequest(val message: String) : ProcessResult()
        data object Success : ProcessResult()
    }

    fun process(csvInputStream: InputStream): ProcessResult {
        when (val result = transactionsCsvReader.read(csvInputStream)) {
            is WrongCsvHeader -> {
                return BadRequest(result.message)
            }

            is MissingCsvField -> {
                return BadRequest(result.message)
            }

            is WrongCsvLine -> {
                return BadRequest(result.message)
            }

            is EmptyCsv -> {
                return BadRequest("Empty CSV file.")
            }

            is Success -> {
                val insertResult = transactionsRepository.insertTransactions(result.transactions)

                // TODO process transactions...
                // TODO send result message
                return ProcessResult.Success
            }
        }
    }
}
