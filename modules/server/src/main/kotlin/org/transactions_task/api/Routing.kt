package org.transactions_task.api

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.bodylimit.RequestBodyLimit
import io.ktor.server.request.contentType
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.utils.io.jvm.javaio.toInputStream
import org.transactions_task.FeatureToggle
import org.transactions_task.Strings
import org.transactions_task.TRANSACTIONS_FILE_SIZE_LIMIT


object Routes {
    const val ROOT = "/"
    const val TRANSACTIONS = "/transactions"
}

fun Application.configureRouting() {
    routing {
        get(Routes.ROOT) {
            call.respondText(Strings.API_DESCRIPTION)
        }

        route(Routes.TRANSACTIONS) {
            setupTransactionsBodyLimit()
            post {
                if (!isCsvContentType()) return@post

                // TODO get by DI
                val transactionsCsvReader = TransactionsCsvReader()

                val ips = call.receiveChannel().toInputStream()

                when (val result = transactionsCsvReader.read(ips)) {
                    is TransactionsCsvReader.CsvReadResult.WrongCsvHeader -> {
                        call.respond(HttpStatusCode.BadRequest, result.message)
                        return@post
                    }

                    is TransactionsCsvReader.CsvReadResult.MissingCsvField -> {
                        call.respond(HttpStatusCode.BadRequest, result.message)
                        return@post
                    }

                    is TransactionsCsvReader.CsvReadResult.EmptyCsv -> {
                        call.respond(HttpStatusCode.BadRequest, "Empty CSV file.")
                        return@post
                    }

//                    is TransactionsCsvReader.CsvReadResult.MissingCsvField -> TODO()
//                    is TransactionsCsvReader.CsvReadResult.Success -> TODO()
                    else -> {
                        // nothing now ... TODO to be removed
                    }
                }
                // TODO process other results...

                call.respondText(Strings.OK)
            }
        }
    }

}

private fun Route.setupTransactionsBodyLimit() {
    install(RequestBodyLimit) {
        bodyLimit { call ->
            when (call.request.httpMethod) {
                HttpMethod.Post -> TRANSACTIONS_FILE_SIZE_LIMIT
                else -> Long.MAX_VALUE
            }
        }
    }
}

private suspend fun RoutingContext.isCsvContentType(): Boolean {
    if (!FeatureToggle.useContentTypeCheck) {
        return true
    }
    if (!call.request.contentType().match(ContentType.Text.CSV)) {
        call.respond(HttpStatusCode.UnsupportedMediaType)
        return false
    }
    return true
}
