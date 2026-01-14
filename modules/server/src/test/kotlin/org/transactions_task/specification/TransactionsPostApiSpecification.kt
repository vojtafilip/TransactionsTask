package org.transactions_task.specification

import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.transactions_task.dsl.getTransactionsAsJson
import org.transactions_task.dsl.postTransactions
import org.transactions_task.dsl.setupApplicationModule
import kotlin.test.Test
import kotlin.test.assertEquals


class TransactionsPostApiSpecification {

    @Test
    fun `should store transactions`() = testApplication {
        // given
        setupApplicationModule()
        val requestBody = """
            reference,timestamp,amount,currency,description
            10000001,2023-01-11T03:00:01Z,20000,CZK,
            10000002,2023-01-11T09:00:00Z,-100,CZK,Lekárna Hradčanská
        """.trimIndent()

        // when
        val response1 = postTransactions(requestBody)
        val response2 = getTransactionsAsJson()

        // then
        assertEquals(HttpStatusCode.OK, response1.status)
        assertEquals("{\"insertedCount\":2,\"failedToInsert\":[]}", response1.bodyAsText())
        assertEquals(HttpStatusCode.OK, response2.status)
        assertEquals(
            """
{"sortedTransactions":[{"timestamp":"2023-01-11T09:00:00Z","amount":-100,"description":"Lekárna Hradčanská","isBiggest":false},{"timestamp":"2023-01-11T03:00:01Z","amount":20000,"description":"","isBiggest":true}],"nextCursor":null}
            """.trimMargin(),
            response2.bodyAsText()
        )
    }

    @Test
    fun `should store transactions if called twice`() = testApplication {
        // given
        setupApplicationModule()
        val requestBody1 = """
            reference,timestamp,amount,currency,description
            10000003,2023-01-11T10:10:10Z,-1337,CZK,Lidl
            10000004,2023-01-11T12:00:00Z,-220,CZK,Šenkýrna
        """.trimIndent()
        val requestBody2 = """
            reference,timestamp,amount,currency,description
            10000001,2023-01-11T03:00:01Z,20000,CZK,
            10000002,2023-01-11T09:00:00Z,-100,CZK,Lekárna Hradčanská
        """.trimIndent()

        // when
        val response1 = postTransactions(requestBody1)
        val response2 = postTransactions(requestBody2)
        val response3 = getTransactionsAsJson()

        // then
        assertEquals(HttpStatusCode.OK, response1.status)
        assertEquals("{\"insertedCount\":2,\"failedToInsert\":[]}", response1.bodyAsText())
        assertEquals(HttpStatusCode.OK, response2.status)
        assertEquals("{\"insertedCount\":2,\"failedToInsert\":[]}", response2.bodyAsText())
        assertEquals(HttpStatusCode.OK, response3.status)
        assertEquals(
            """
{"sortedTransactions":[{"timestamp":"2023-01-11T12:00:00Z","amount":-220,"description":"Šenkýrna","isBiggest":false},{"timestamp":"2023-01-11T10:10:10Z","amount":-1337,"description":"Lidl","isBiggest":false},{"timestamp":"2023-01-11T09:00:00Z","amount":-100,"description":"Lekárna Hradčanská","isBiggest":false},{"timestamp":"2023-01-11T03:00:01Z","amount":20000,"description":"","isBiggest":true}],"nextCursor":null}
            """.trimMargin(),
            response3.bodyAsText()
        )
    }

    @Test
    fun `should ignore already stored transactions`() = testApplication {
        // given
        setupApplicationModule()
        val requestBody1 = """
            reference,timestamp,amount,currency,description
            10000001,2023-01-11T03:00:01Z,20000,CZK,
            10000002,2023-01-11T09:00:00Z,-100,CZK,Lekárna Hradčanská
        """.trimIndent()
        val requestBody2 = """
            reference,timestamp,amount,currency,description
            10000001,2023-01-11T03:00:01Z,20000,CZK,DUPLICATE
            10000002,2023-01-11T09:00:00Z,-100,CZK,DUPLICATE
        """.trimIndent()

        // when
        val response1 = postTransactions(requestBody1)
        val response2 = postTransactions(requestBody2)
        val response3 = getTransactionsAsJson()

        // then
        assertEquals(HttpStatusCode.OK, response1.status)
        assertEquals("{\"insertedCount\":2,\"failedToInsert\":[]}", response1.bodyAsText())
        assertEquals(HttpStatusCode.OK, response2.status)
        assertEquals("{\"insertedCount\":0,\"failedToInsert\":[10000001,10000002]}", response2.bodyAsText())
        assertEquals(HttpStatusCode.OK, response3.status)
        assertEquals(
            """
{"sortedTransactions":[{"timestamp":"2023-01-11T09:00:00Z","amount":-100,"description":"Lekárna Hradčanská","isBiggest":false},{"timestamp":"2023-01-11T03:00:01Z","amount":20000,"description":"","isBiggest":true}],"nextCursor":null}
            """.trimMargin(),
            response3.bodyAsText()
        )

    }
}
