package org.transactions_task.specification

import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.transactions_task.FeatureToggle
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
        assertEquals("", response.bodyAsText())
    }

    @Test
    fun `should fail on wrong content type`() = testApplication {

        // Skip if feature switched off ... TODO mark test as skipped
        if (!FeatureToggle.useContentTypeCheck) return@testApplication

        // given
        setupApplicationModule()
        val requestBody = """
            reference,timestamp,amount,currency,description
            10000002,2023-01-11T09:00:00Z,-100,CZK,Lekárna Hradčanská
        """
            .trimIndent()

        // when
        val response = postTransactions(requestBody, ContentType.Text.Xml)

        // then
        assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        assertEquals("", response.bodyAsText())
    }

    @Test
    fun `should fail on wrong format`() = testApplication {
        // given
        setupApplicationModule()
        val requestBody = "this is not CSV file\nhas no records"

        // when
        val response = postTransactions(requestBody)

        // then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Key reference is missing in the map.", response.bodyAsText())
    }

    @Test
    fun `should fail on empty CSV`() = testApplication {
        // given
        setupApplicationModule()
        val requestBody = "reference,timestamp,amount,currency,description"

        // when
        val response = postTransactions(requestBody)

        // then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Empty CSV file.", response.bodyAsText())
    }

    @Test
    fun `should fail on missing column`() = testApplication {
        // given
        setupApplicationModule()
        val requestBody = """
            reference,timestamp,amount,currency
            10000002,2023-01-11T09:00:00Z,-100,CZK
        """
            .trimIndent()

        // when
        val response = postTransactions(requestBody)

        // then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Key description is missing in the map.", response.bodyAsText())
    }

    @Test
    fun `should fail on missing field in a row`() = testApplication {
        // given
        setupApplicationModule()
        val requestBody = """
            reference,timestamp,amount,currency,description
            10000002,2023-01-11T09:00:00Z,-100,CZK
        """
            .trimIndent()

        // when
        val response = postTransactions(requestBody)

        // then
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(
            "Fields num seems to be 5 on each row, but on 1th csv row, fields num is 4.",
            response.bodyAsText()
        )
    }
}
