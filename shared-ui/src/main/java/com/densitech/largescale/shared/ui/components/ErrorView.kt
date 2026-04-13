package com.densitech.largescale.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.densitech.largescale.shared.ui.theme.AppTheme
import com.densitech.largescale.shared.ui.theme.Spacing

/**
 * Full-screen error state with optional retry action.
 *
 * @param message     Human-readable error description
 * @param modifier    Modifier applied to the root [Column]
 * @param title       Short error title (default "Something went wrong")
 * @param icon        Icon displayed above the title
 * @param retryLabel  Label for the retry button (default "Try again")
 * @param onRetry     When non-null, a retry button is shown and calls this lambda on tap
 */
@Composable
fun ErrorView(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "Something went wrong",
    icon: ImageVector = Icons.Outlined.ErrorOutline,
    retryLabel: String = "Try again",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(Modifier.height(Spacing.lg))
            AppButton(text = retryLabel, onClick = onRetry)
        }
    }
}

/**
 * Convenience overload for network/connectivity errors.
 */
@Composable
fun NetworkErrorView(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    ErrorView(
        message = "Check your connection and try again.",
        modifier = modifier,
        title = "No connection",
        icon = Icons.Outlined.WifiOff,
        onRetry = onRetry
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    AppTheme {
        ErrorView(
            message = "Failed to load orders. Please try again.",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworkErrorViewPreview() {
    AppTheme {
        NetworkErrorView(onRetry = {})
    }
}
