package com.densitech.largescale.shared.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.densitech.largescale.shared.ui.theme.AppTheme
import com.densitech.largescale.shared.ui.theme.Spacing

/**
 * Elevated card — use for content that needs visual separation from the background.
 *
 * @param modifier  Modifier applied to the card surface
 * @param onClick   When non-null, the card becomes clickable
 * @param content   Slot for card body — already padded by [Spacing.md]
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs)
        ) {
            Column(modifier = Modifier.padding(Spacing.md), content = content)
        }
    } else {
        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs)
        ) {
            Column(modifier = Modifier.padding(Spacing.md), content = content)
        }
    }
}

/**
 * Outlined card — use for items in a list where elevation would cause visual noise.
 *
 * @param modifier  Modifier applied to the card surface
 * @param onClick   When non-null, the card becomes clickable
 * @param content   Slot for card body — already padded by [Spacing.md]
 */
@Composable
fun AppOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        OutlinedCard(
            onClick = onClick,
            modifier = modifier
        ) {
            Column(modifier = Modifier.padding(Spacing.md), content = content)
        }
    } else {
        OutlinedCard(modifier = modifier) {
            Column(modifier = Modifier.padding(Spacing.md), content = content)
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AppCardPreview() {
    AppTheme {
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text("Order #1234", style = MaterialTheme.typography.titleMedium)
            Text("2 items · $48.00", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppOutlinedCardPreview() {
    AppTheme {
        AppOutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Text("Inventory Item", style = MaterialTheme.typography.titleMedium)
            Text("Stock: 42 units", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
