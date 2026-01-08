package org.transactions_task

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ComposeAppWebTest {

    @Test
    fun example() = runComposeUiTest {
        // given
        setContent { App() }
        onNodeWithText("Hello", substring = true)
            .assertDoesNotExist()

        // when
        onNodeWithText("Click me!")
            .performClick()

        // then
        onNodeWithText("Hello", substring = true)
            .assertExists()
    }
}
