package org.transactions_task

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.coroutines.delay
import org.transactions_task.api.Api
import org.transactions_task.model.Transaction
import org.transactions_task.repository.TransactionsRepository
import org.transactions_task.screen.transactions.TransactionsScreen
import org.transactions_task.screen.transactions.TransactionsViewModel
import kotlin.test.Test
import kotlin.time.Clock

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

    @Test
    fun `should show transaction on successful loading`() = runComposeUiTest {
        // given
        val transactions = (1..10).map {
            Transaction(
                timestamp = Clock.System.now(),
                amount = it.toLong(),
                description = "transaction $it",
                isBiggest = false
            )
        }

        // when
        setContent {
            TransactionsScreen(
                createViewModel(
                    Api.LoadResponse.Success(transactions)
                )
            )
        }

        // then
        onNodeWithTag("transactionsList").assertExists()
        onNodeWithText("transaction 1").assertExists()
        onNodeWithText("transaction 5").assertExists()
        onNodeWithText("transaction 10").assertExists()
    }

    @Test
    fun `should mark transaction with biggest amount`() = runComposeUiTest {
        // given
        val biggest = 3
        val transactions = (1..5).map {
            Transaction(
                timestamp = Clock.System.now(),
                amount = it.toLong(),
                description = "transaction $it",
                isBiggest = it == biggest
            )
        }

        // when
        setContent {
            TransactionsScreen(
                createViewModel(
                    Api.LoadResponse.Success(transactions)
                )
            )
        }

        // then
        onNodeWithTag("biggestTransaction - 1").assertDoesNotExist()
        onNodeWithTag("biggestTransaction - 2").assertDoesNotExist()
        onNodeWithTag("biggestTransaction - 3").assertExists()
        onNodeWithTag("biggestTransaction - 4").assertDoesNotExist()
        onNodeWithTag("biggestTransaction - 5").assertDoesNotExist()
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
