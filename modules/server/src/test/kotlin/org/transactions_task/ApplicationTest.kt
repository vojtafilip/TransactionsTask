package org.transactions_task

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.transactions_task.dsl.setupApplicationModule
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        setupApplicationModule()

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("This is a simple API for transactions management.", response.bodyAsText())
    }
}