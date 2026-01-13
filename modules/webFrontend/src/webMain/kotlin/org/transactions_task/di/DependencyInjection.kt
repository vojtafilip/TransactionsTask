package org.transactions_task.di

import org.transactions_task.api.HttpApi
import org.transactions_task.repository.TransactionsRepository
import org.transactions_task.screen.transactions.TransactionsViewModel

private val transactionsViewModel: TransactionsViewModel by lazy {
    TransactionsViewModel(
        TransactionsRepository(
            HttpApi()
        )
    )
}

/**
 * This is oversimplified DI...
 * Because of single page and Web we just create singletons here.
 * TODO: use proper DI.
 */
fun viewModel(): TransactionsViewModel {
    return transactionsViewModel
}
