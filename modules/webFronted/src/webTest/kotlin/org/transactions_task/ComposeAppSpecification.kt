package org.transactions_task

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.coroutines.delay
import org.transactions_task.api.Api
import org.transactions_task.repository.TransactionsRepository
import org.transactions_task.screen.transactions.TransactionsScreen
import org.transactions_task.screen.transactions.TransactionsViewModel
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ComposeAppSpecification {

    @Test
    fun `should show loading indicator on loading`() = runComposeUiTest {
        setContent {
            TransactionsScreen(createViewModel(null))
        }
        onNodeWithTag("loadingIndicator").assertExists()
    }

    @Test

    fun `should show error indicator on loading error`() = runComposeUiTest {
        setContent {
            TransactionsScreen(
                createViewModel(
                    Api.LoadResponse.Error("test error")
                )
            )
        }

        // then
        onNodeWithTag("errorIndicator").assertExists()
        onNodeWithText("test error").assertExists()
    }
}

private fun createViewModel(
    /**
     * Null means loading.
     */
    fakeApiResponse: Api.LoadResponse?
) = TransactionsViewModel(
    TransactionsRepository(
        object : Api {
            override suspend fun loadTransactions(): Api.LoadResponse {
                if (fakeApiResponse != null) return fakeApiResponse
                delay(Long.MAX_VALUE)
                error("Should never happen")
            }
        }
    )
)
