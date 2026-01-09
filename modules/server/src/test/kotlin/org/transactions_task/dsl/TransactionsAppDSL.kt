package org.transactions_task.dsl

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import org.transactions_task.module

fun ApplicationTestBuilder.setupApplicationModule() {
    application {
        module()
    }
}

suspend fun ApplicationTestBuilder.postTransactions(requestBody: String): HttpResponse =
    client.post("/transactions") {
        contentType(ContentType.Text.CSV)
        setBody(requestBody)
    }
