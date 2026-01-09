package org.transactions_task.specification

import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.transactions_task.dsl.postTransactions
import org.transactions_task.dsl.setupApplicationModule
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionsPostApiFailsSpecification {

    @Test
    fun `should fail on big input`() = testApplication {
        // given
        setupApplicationModule()
        val requestBody = """
            reference,timestamp,amount,currency,description
            10000001,2023-01-11T03:00:01Z,20000,CZK,            
        """
            .trimIndent() + (0..100_000)
            .joinToString("\n") {
                "10000002,2023-01-11T09:00:00Z,-100,CZK,Lekárna Hradčanská"
            }

        // when
        val response = postTransactions(requestBody)

        // then
        assertEquals(HttpStatusCode.PayloadTooLarge, response.status)
    }
}
