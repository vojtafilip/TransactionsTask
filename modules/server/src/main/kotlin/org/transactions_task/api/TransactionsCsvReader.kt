package org.transactions_task.api

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.util.CSVFieldNumDifferentException
import java.io.InputStream
import kotlin.time.Instant

class TransactionsCsvReader {
    private val csvReader = csvReader {
        // TODO setup if needed
    }

    sealed class CsvReadResult {
        data class WrongCsvHeader(val message: String) : CsvReadResult()
        data object EmptyCsv : CsvReadResult()
        data class MissingCsvField(val message: String) : CsvReadResult()
        data class Success(val transactions: List<TransactionLine>) : CsvReadResult()
    }

    fun read(ips: InputStream): CsvReadResult {
        // TODO use csvReader.open(ips) and readAllWithHeaderAsSequence() to read as stream

        val transactions: List<TransactionLine> =
            try {
                readCsv(ips)
            } catch (e: NoSuchElementException) {
                return CsvReadResult.WrongCsvHeader(e.message ?: "Unknown error")
            } catch (e: CSVFieldNumDifferentException) {
                // TODO setup csvReader with insufficientFieldsRowBehaviour and excessFieldsRowBehaviour and check it for each line
                return CsvReadResult.MissingCsvField(e.message ?: "Unknown error")
            }

        if (transactions.isEmpty()) return CsvReadResult.EmptyCsv

        return CsvReadResult.Success(transactions)
    }

    private fun readCsv(ips: InputStream): List<TransactionLine> =
        csvReader
            .readAllWithHeader(ips)
            .map { CsvLine(it) }
            .map { it.toTransactionLine() }

}

private class CsvLine(map: Map<String, String>) {
    val reference: String by map
    val timestamp: String by map
    val amount: String by map
    val currency: String by map
    val description: String by map
}

private fun CsvLine.toTransactionLine() =
    TransactionLine(
        reference.toLong(),
        Instant.parse(timestamp),
        amount.toLong(),
        if (currency == "CZK") TransactionLine.Currency.CZK else error("Wrong currency"), // TODO use sophisticated parsing
        description.ifBlank { null }
    )

// TODO move to a model
data class TransactionLine(
    val reference: Long,
    val timestamp: Instant,
    val amount: Long,
    val currency: Currency,
    val description: String?
) {

    // TODO separate
    enum class Currency {
        CZK
    }
}
