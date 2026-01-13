package org.transactions_task.service

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.util.CSVFieldNumDifferentException
import io.ktor.util.logging.KtorSimpleLogger
import org.transactions_task.domain.model.Currency
import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord
import java.io.InputStream
import kotlin.time.Instant

private val LOGGER = KtorSimpleLogger(TransactionsCsvReader::class.java.name)

class TransactionsCsvReader {
    private val csvReader = csvReader {
        // TODO setup if needed
    }

    sealed class CsvReadResult {
        data class WrongCsvHeader(val message: String) : CsvReadResult()
        data object EmptyCsv : CsvReadResult()
        data class MissingCsvField(val message: String) : CsvReadResult()
        data class WrongCsvLine(val message: String) : CsvReadResult()
        data class Success(val transactions: List<TransactionRecord>) : CsvReadResult()
    }

    fun read(ips: InputStream): CsvReadResult {
        // TODO use csvReader.open(ips) and readAllWithHeaderAsSequence() to read as stream

        val transactions: List<TransactionRecord> =
            try {
                readCsv(ips)
            } catch (e: NoSuchElementException) {
                return CsvReadResult.WrongCsvHeader(e.message ?: "Unknown error")
            } catch (e: CSVFieldNumDifferentException) {
                // TODO setup csvReader with insufficientFieldsRowBehaviour and excessFieldsRowBehaviour and check it for each line
                return CsvReadResult.MissingCsvField(e.message ?: "Unknown error")
            } catch (e: WrongCsvLineException) {
                // TODO move logger to more general place
                LOGGER.error("Failed to parse CSV line ${e.lineNumber}.", e)

                // TODO log to logger, propagate info about line
                return CsvReadResult.WrongCsvLine(e.message ?: "Unknown error")
            }

        if (transactions.isEmpty()) return CsvReadResult.EmptyCsv

        return CsvReadResult.Success(transactions)
    }

    private fun readCsv(ips: InputStream): List<TransactionRecord> =
        csvReader
            .readAllWithHeader(ips)
            .map { CsvLine(it) }
            .mapIndexed { i, line -> line.toTransactionRecord(i) }

}

private class CsvLine(map: Map<String, String>) {
    val reference: String by map
    val timestamp: String by map
    val amount: String by map
    val currency: String by map
    val description: String by map
}

private fun CsvLine.toTransactionRecord(lineNumber: Int) =
    try {
        TransactionRecord(
            Reference(reference.toLong()),
            Instant.parse(timestamp),
            amount.toLong(),
            if (currency == "CZK") Currency.CZK else error("Wrong currency"), // TODO use sophisticated parsing
            description.ifBlank { null }
                ?.trim()
        )
    } catch (e: NoSuchElementException) {
        throw e
    } catch (e: Exception) {
        throw WrongCsvLineException(lineNumber, "Failed to parse CSV line.", e)
    }

private class WrongCsvLineException(
    val lineNumber: Int,
    message: String,
    cause: Throwable
) : Exception(message, cause)
