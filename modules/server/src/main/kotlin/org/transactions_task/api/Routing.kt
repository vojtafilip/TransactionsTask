package org.transactions_task.api

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
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
import org.koin.ktor.ext.inject
import org.transactions_task.FeatureToggle
import org.transactions_task.Strings
import org.transactions_task.api.dto.TransactionsPostResponseDTO
import org.transactions_task.service.GetTransactionsService
import org.transactions_task.service.PostTransactionsService


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
            val postTransactionsService by inject<PostTransactionsService>()
            val getTransactionsService by inject<GetTransactionsService>()

            setupTransactionsPostBodyLimit()
            post { processTransactionsPost(postTransactionsService) }
            get { processTransactionsGet(getTransactionsService) }
        }
    }

}

private fun Route.setupTransactionsPostBodyLimit() {

    // TODO separate to a configuration logic, to process of application configs at one place
    val transactionsFileSizeLimit: Long = environment.config
        .propertyOrNull("ktor.transactions.fileSizeLimit")!!
        .getString().toLong()

    install(RequestBodyLimit) {
        bodyLimit { call ->
            when (call.request.httpMethod) {
                HttpMethod.Post -> transactionsFileSizeLimit
                else -> Long.MAX_VALUE
            }
        }
    }
}

private suspend fun RoutingContext.processTransactionsPost(
    postTransactionsService: PostTransactionsService
) {
    if (!isCsvContentType()) return

    val ips = call.receiveChannel().toInputStream()

    when (
        val result = postTransactionsService.process(ips)
    ) {
        is PostTransactionsService.ProcessResult.BadRequest -> {
            call.respond(HttpStatusCode.BadRequest, result.message)
        }

        is PostTransactionsService.ProcessResult.Success -> {
            call.respond(
                HttpStatusCode.OK,
                TransactionsPostResponseDTO(
                    result.insertedCount,
                    result.failedToInsert.map { it.ref }
                ))
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

private suspend fun RoutingContext.processTransactionsGet(
    getTransactionsService: GetTransactionsService
) {
    val format = call.request.queryParameters["format"]

    val getTransactionsResult = getTransactionsService.getTransactions()

    if (format == "json") {
        call.respond(
            HttpStatusCode.OK,
            getTransactionsResult.toDTO()
        )
    } else {
        call.respondHtml(
            HttpStatusCode.OK
        ) {
            transactionsResultToHtml(getTransactionsResult)
        }
    }
}
