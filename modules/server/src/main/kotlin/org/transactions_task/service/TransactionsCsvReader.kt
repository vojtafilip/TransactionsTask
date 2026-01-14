package org.transactions_task.service

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.util.CSVFieldNumDifferentException
import io.ktor.util.logging.KtorSimpleLogger
import org.transactions_task.Config.TRANSACTION_DESCRIPTION_MAX_LENGTH
import org.transactions_task.domain.model.Currency
import org.transactions_task.domain.model.Reference
import org.transactions_task.domain.model.TransactionRecord
import org.transactions_task.service.TransactionsCsvReader.CsvValidationResult.*
import java.io.InputStream
import kotlin.time.Instant

private val LOGGER = KtorSimpleLogger(TransactionsCsvReader::class.java.name)

class TransactionsCsvReader {
    private val csvReader = csvReader {
        // TODO setup if needed
    }

    sealed class CsvValidationResult {
        data class WrongCsvHeader(val message: String) : CsvValidationResult()
        data object EmptyCsv : CsvValidationResult()
        data class MissingCsvField(val message: String) : CsvValidationResult()
        data class WrongCsvLine(val message: String) : CsvValidationResult()
        data object Success : CsvValidationResult()
    }

    // TODO more detailed validations, don't stop at first invalid line
    fun validate(ips: InputStream): CsvValidationResult {
        val processedLines = try {
            processCsv(ips) {}
        } catch (e: NoSuchElementException) {
            return WrongCsvHeader(e.message ?: "Unknown error")
        } catch (e: CSVFieldNumDifferentException) {
            // TODO setup csvReader with insufficientFieldsRowBehaviour and excessFieldsRowBehaviour and check it for each line
            return MissingCsvField(e.message ?: "Unknown error")
        } catch (e: WrongCsvLineException) {
            // TODO move logger to more general place
            LOGGER.error("Failed to parse CSV line ${e.lineNumber}.", e)

            // TODO propagate info about line
            return WrongCsvLine(e.message ?: "Unknown error")
        }

        if (processedLines == 0) return EmptyCsv

        return Success
    }

    fun read(ips: InputStream, onEach: (TransactionRecord) -> Unit) {
        // no try-catch - we expect CSV is already validated
        processCsv(ips, onEach)
    }

    private fun processCsv(ips: InputStream, onEach: (TransactionRecord) -> Unit): Int {
        var processedLines = 0
        csvReader.open(ips) {
            readAllWithHeaderAsSequence()
                .map { CsvLine(it) }
                .mapIndexed { i, line -> line.toTransactionRecord(i + 1) }
                .forEach {
                    onEach(it)
                    processedLines++
                }
        }
        return processedLines
    }

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
                ?.also {
                    require(it.length <= TRANSACTION_DESCRIPTION_MAX_LENGTH) {
                        "Description is too long."
                    }
                }
        )
    } catch (e: NoSuchElementException) {
        throw e
    } catch (e: Exception) {
        throw WrongCsvLineException(
            lineNumber,
            "Failed to parse CSV line $lineNumber: ${e.message ?: "Unknown reason."}",
            e
        )
    }

private class WrongCsvLineException(
    val lineNumber: Int,
    message: String,
    cause: Throwable
) : Exception(message, cause)
