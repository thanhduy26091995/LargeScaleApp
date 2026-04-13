package com.densitech.largescale.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.densitech.largescale.contracts.TenantTheme

// ── Default color schemes ─────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary = Brand40,
    onPrimary = Color.White,
    primaryContainer = BrandContainer40,
    onPrimaryContainer = Neutral10,
    secondary = Secondary40,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer40,
    onSecondaryContainer = Neutral10,
    tertiary = Tertiary40,
    onTertiary = Color.White,
    error = Error40,
    onError = Color.White,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    outline = NeutralVariant60
)

private val DarkColorScheme = darkColorScheme(
    primary = Brand80,
    onPrimary = Neutral10,
    primaryContainer = BrandContainer80,
    onPrimaryContainer = BrandContainer40,
    secondary = Secondary80,
    onSecondary = Neutral10,
    secondaryContainer = SecondaryContainer80,
    onSecondaryContainer = SecondaryContainer40,
    tertiary = Tertiary80,
    onTertiary = Neutral10,
    error = Error80,
    onError = Neutral10,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral20,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    outline = NeutralVariant60
)

// ── CompositionLocal for Spacing ──────────────────────────────────────────────

val LocalSpacing = staticCompositionLocalOf { Spacing }

// ── AppTheme ──────────────────────────────────────────────────────────────────

/**
 * Root theme composable for the entire app.
 *
 * Supports tenant-aware color overrides via [tenantTheme].
 * When [tenantTheme] is provided, its hex color strings are parsed and
 * merged into the Material 3 color scheme.
 *
 * Usage:
 * ```
 * AppTheme(tenantTheme = moduleContext.tenantConfig.value?.theme) {
 *     // screens
 * }
 * ```
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    tenantTheme: TenantTheme? = null,
    content: @Composable () -> Unit
) {
    val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val colorScheme = if (tenantTheme != null) {
        val primary = tenantTheme.primaryColor.toComposeColorOrNull()
        val secondary = tenantTheme.secondaryColor.toComposeColorOrNull()
        val background = tenantTheme.backgroundColor.toComposeColorOrNull()

        baseScheme.copy(
            primary = primary ?: baseScheme.primary,
            secondary = secondary ?: baseScheme.secondary,
            background = background ?: baseScheme.background,
            surface = background ?: baseScheme.surface
        )
    } else {
        baseScheme
    }

    CompositionLocalProvider(LocalSpacing provides Spacing) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────
/**
 * Parse a hex color string (e.g. "#1565C0") to a Compose [Color].
 * Returns null if the string is blank or unparseable.
 */
private fun String.toComposeColorOrNull(): Color? = runCatching {
    Color(android.graphics.Color.parseColor(this))
}.getOrNull()
