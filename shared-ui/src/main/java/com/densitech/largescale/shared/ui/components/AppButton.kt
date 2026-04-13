package com.densitech.largescale.shared.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.densitech.largescale.shared.ui.theme.AppTheme
import com.densitech.largescale.shared.ui.theme.Spacing

/**
 * Primary filled button. Use for the single most important action on a screen.
 *
 * @param text       Button label
 * @param onClick    Click handler
 * @param modifier   Modifier
 * @param enabled    When false, the button is dimmed and non-interactive
 * @param isLoading  When true, replaces label with a spinner (disables click)
 * @param leadingIcon Optional icon shown before the label
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            leadingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * Secondary outlined button. Use for secondary actions alongside a primary button.
 */
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        leadingIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Tertiary text-only button. Use for low-emphasis actions (e.g., "Cancel", "Skip").
 */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    AppTheme {
        AppButton(text = "Confirm Order", onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonLoadingPreview() {
    AppTheme {
        AppButton(text = "Confirm Order", onClick = {}, isLoading = true)
    }
}

@Preview(showBackground = true)
@Composable
private fun AppOutlinedButtonPreview() {
    AppTheme {
        AppOutlinedButton(text = "Cancel", onClick = {})
    }
}
