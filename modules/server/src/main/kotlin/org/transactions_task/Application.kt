package org.transactions_task

import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.bodylimit.RequestBodyLimit
import io.ktor.server.request.httpMethod
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        get(Routes.ROOT) {
            call.respondText(Strings.API_DESCRIPTION)
        }

        route(Routes.TRANSACTIONS) {
            setupTransactionsBodyLimit()

            post {
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
