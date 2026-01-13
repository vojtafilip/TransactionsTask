package org.transactions_task.dsl

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.config.mergeWith
import io.ktor.server.testing.ApplicationTestBuilder

fun ApplicationTestBuilder.setupApplicationModule() {
    environment {
        config = ApplicationConfig("application.conf")
            .mergeWith(MapApplicationConfig(
                "ktor.db.dropTables" to "true")
            )
    }
// Alternative:
//    application {
//        module(testing = true)
//    }
}

suspend fun ApplicationTestBuilder.postTransactions(
    requestBody: String,
    contentType: ContentType = ContentType.Text.CSV
): HttpResponse =
    client.post("/transactions") {
        contentType(contentType)
        setBody(requestBody)
    }

suspend fun ApplicationTestBuilder.getTransactionsAsJson(): HttpResponse =
    client.get("/transactions?format=json")
