package com.densitech.largescale.shared.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Consistent spacing scale used across the app.
 *
 * Use these instead of hardcoded dp values to keep layout consistent:
 * ```
 * Modifier.padding(Spacing.md)
 * Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
 * ```
 */
object Spacing {
    val xxs = 2.dp
    val xs  = 4.dp
    val sm  = 8.dp
    val md  = 16.dp
    val lg  = 24.dp
    val xl  = 32.dp
    val xxl = 48.dp

    /** Standard horizontal screen edge padding. */
    val screenHorizontal = 16.dp

    /** Standard top padding below a top bar. */
    val screenTop = 8.dp
}
