package org.transactions_task.dsl

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import org.transactions_task.module

fun ApplicationTestBuilder.setupApplicationModule() {
    application {
        module(testing = true)
    }
}

suspend fun ApplicationTestBuilder.postTransactions(
    requestBody: String,
    contentType: ContentType = ContentType.Text.CSV
): HttpResponse =
    client.post("/transactions") {
        contentType(contentType)
        setBody(requestBody)
    }

suspend fun ApplicationTestBuilder.getTransactions(): HttpResponse =
    client.get("/transactions?format=json")
