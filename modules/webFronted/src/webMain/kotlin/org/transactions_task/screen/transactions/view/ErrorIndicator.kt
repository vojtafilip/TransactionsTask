package org.transactions_task.screen.transactions.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

@Composable
fun ErrorIndicator(
    modifier: Modifier = Modifier.Companion,
    message: String
) {
    Column(
        modifier = modifier.testTag("errorIndicator"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Loading error...", color = Color.Red)
        Text(message)

        // TODO "load again: button
    }
}