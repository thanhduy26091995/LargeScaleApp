package com.densitech.largescale.shared.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.densitech.largescale.shared.ui.theme.AppTheme
import com.densitech.largescale.shared.ui.theme.Spacing

/**
 * Full-screen centered loading state.
 *
 * @param message Optional loading message shown below the spinner
 * @param modifier Modifier applied to the root [Box]
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
            if (message != null) {
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Inline (non-full-screen) loading spinner. Use inside cards or list items.
 *
 * @param modifier Modifier
 * @param size     Spinner diameter (default 24.dp)
 */
@Composable
fun InlineLoadingIndicator(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 24.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 2.dp
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun LoadingIndicatorPreview() {
    AppTheme {
        LoadingIndicator(message = "Loading orders...")
    }
}
