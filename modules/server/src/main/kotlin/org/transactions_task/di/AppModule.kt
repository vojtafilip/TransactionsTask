package org.transactions_task.di

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.transactions_task.repository.ExposedTransactionsRepository
import org.transactions_task.repository.TransactionsRepository
import org.transactions_task.service.GetTransactionsService
import org.transactions_task.service.PostTransactionsService

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule)
    }
}

val appModule = module {
    singleOf(::ExposedTransactionsRepository) { bind<TransactionsRepository>() }
    singleOf(::PostTransactionsService)
    singleOf(::GetTransactionsService)
}
