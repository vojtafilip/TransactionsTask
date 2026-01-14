package org.transactions_task.service

import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.copyTo
import org.transactions_task.domain.model.Reference
import org.transactions_task.repository.TransactionsRepository
import org.transactions_task.service.TransactionsCsvReader.CsvValidationResult.EmptyCsv
import org.transactions_task.service.TransactionsCsvReader.CsvValidationResult.MissingCsvField
import org.transactions_task.service.TransactionsCsvReader.CsvValidationResult.Success
import org.transactions_task.service.TransactionsCsvReader.CsvValidationResult.WrongCsvHeader
import org.transactions_task.service.TransactionsCsvReader.CsvValidationResult.WrongCsvLine
import org.transactions_task.service.PostTransactionsService.ProcessResult.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import kotlin.io.path.createTempFile

class PostTransactionsService(
    private val transactionsRepository: TransactionsRepository
) {
    private val transactionsCsvReader = TransactionsCsvReader()

    sealed class ProcessResult() {
        data class BadRequest(val message: String) : ProcessResult()
        data class Success(
            val insertedCount: Int,
            val failedToInsert: List<Reference>
        ) : ProcessResult()
    }

    suspend fun process(csvChannel: ByteReadChannel): ProcessResult {
        val tempFile = createTempFile(csvChannel)
        try {
            return process(tempFile)
        } finally {
            tempFile.delete()
        }
    }

    private suspend fun createTempFile(csvChannel: ByteReadChannel): File {
        val tempFile = createTempFile().toFile()
        tempFile.deleteOnExit()
        copyToTempFile(tempFile, csvChannel)
        return tempFile
    }

    private suspend fun copyToTempFile(tempFile: File, channel: ByteReadChannel) {
        val fileChannel = tempFile.writeChannel()
        try {
            channel.copyTo(fileChannel)
        } finally {
            fileChannel.flushAndClose()
        }
    }

    private fun process(tempFile: File): ProcessResult {
        tempFile.inputStream().use { csvInputStream ->
            val result = validate(csvInputStream)
            if (result != null) {
                return BadRequest(result)
            }
        }

        tempFile.inputStream().use { csvInputStream ->
            return processTransactions(csvInputStream)
        }
    }

    private fun validate(ips: InputStream): String? =
        when (
            val result = transactionsCsvReader.validate(ips)
        ) {
            is WrongCsvHeader -> result.message
            is MissingCsvField -> result.message
            is WrongCsvLine -> result.message
            is EmptyCsv -> "Empty CSV file."
            is Success -> null
        }

    private fun processTransactions(csvInputStream: FileInputStream): ProcessResult.Success {
        var insertedCount = 0
        val failedToInsert = mutableListOf<Reference>()

        transactionsCsvReader.read(csvInputStream) { transaction ->
            val inserted = transactionsRepository.insertTransaction(transaction)
            if (inserted) {
                insertedCount++
            } else {
                failedToInsert.add(transaction.reference)
            }
        }

        return Success(
            insertedCount,
            failedToInsert
        )
    }
}
