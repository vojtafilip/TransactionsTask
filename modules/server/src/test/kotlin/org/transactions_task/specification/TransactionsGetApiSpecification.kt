package org.transactions_task.specification

import io.ktor.server.testing.testApplication
import org.transactions_task.api.dto.TransactionsGetResponseDTO.TransactionDTO
import org.transactions_task.dsl.getTransactionsAsDTO
import org.transactions_task.dsl.postTransactions
import org.transactions_task.dsl.setupApplicationModule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TransactionsGetApiSpecification {

    @Test
    fun `should get stored transactions`() = testApplication {
        // given
        setupApplicationModule()
        postTransactions(testData)
        val expected = expectedResponse

        // when
        val responseDTO = getTransactionsAsDTO()

        // then
        assertEquals(7, responseDTO.sortedTransactions.size)
        assertEquals(expected, responseDTO.sortedTransactions)
        assertNull(responseDTO.nextCursor)
    }

    @Test
    fun `should get stored transactions with limit`() = testApplication {
        // given
        setupApplicationModule()
        postTransactions(testData)
        val limit = 3

        // when
        val responseDTO = getTransactionsAsDTO(limit = limit)

        // then
        assertEquals(limit, responseDTO.sortedTransactions.size)
        assertEquals(
            listOf("2023-01-11T17:00:00Z", "2023-01-11T13:00:01Z", "2023-01-11T13:00:01Z"),
            responseDTO.sortedTransactions.map { it.timestamp }
        )
        assertNotNull(responseDTO.nextCursor)
    }

    @Test
    fun `should get stored transactions by cursor`() = testApplication {
        // given
        setupApplicationModule()
        postTransactions(testData)
        val limit1 = 2
        val limit2 = 3

        // when
        val responseDTO1 = getTransactionsAsDTO(limit = limit1)
        val responseDTO2 = getTransactionsAsDTO(limit = limit2, cursor = responseDTO1.nextCursor)

        // then
        assertEquals(limit2, responseDTO2.sortedTransactions.size)
        assertEquals(
            listOf("2023-01-11T13:00:01Z", "2023-01-11T12:00:00Z", "2023-01-11T10:10:10Z"),
            responseDTO2.sortedTransactions.map { it.timestamp }
        )
    }
}

private val testData = """
reference,timestamp,amount,currency,description
10000001,2023-01-11T03:00:01Z,20000,CZK,
10000002,2023-01-11T09:00:00Z,-100,CZK,Lekárna Hradčanská
10000003,2023-01-11T10:10:10Z,-1337,CZK,Lidl
10000004,2023-01-11T12:00:00Z,-220,CZK,Šenkýrna
10000005,2023-01-11T13:00:01Z,20000,CZK,Kofii
10000006,2023-01-11T17:00:00Z,-12000,CZK,Servis Škoda Praha
10000007,2023-01-11T13:00:01Z,100,CZK,Pepa
""".trimIndent()

private val expectedResponse = listOf(
    TransactionDTO(
        timestamp = "2023-01-11T17:00:00Z",
        amount = -12000,
        description = "Servis Škoda Praha",
        isBiggest = false
    ),
    TransactionDTO(
        timestamp = "2023-01-11T13:00:01Z",
        amount = 100,
        description = "Pepa",
        isBiggest = false
    ),
    TransactionDTO(
        timestamp = "2023-01-11T13:00:01Z",
        amount = 20000,
        description = "Kofii",
        isBiggest = true
    ),
    TransactionDTO(
        timestamp = "2023-01-11T12:00:00Z",
        amount = -220,
        description = "Šenkýrna",
        isBiggest = false
    ),
    TransactionDTO(
        timestamp = "2023-01-11T10:10:10Z",
        amount = -1337,
        description = "Lidl",
        isBiggest = false
    ),
    TransactionDTO(
        timestamp = "2023-01-11T09:00:00Z",
        amount = -100,
        description = "Lekárna Hradčanská",
        isBiggest = false
    ),
    TransactionDTO(
        timestamp = "2023-01-11T03:00:01Z",
        amount = 20000,
        description = "",
        isBiggest = true
    )
)
