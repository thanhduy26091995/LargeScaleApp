package com.densitech.largescale.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.densitech.largescale.shared.ui.theme.AppTheme

/**
 * Circular avatar that shows initials when no image URL is available.
 *
 * Phase 5+: When an image loading library (Coil) is added, replace the
 * initials fallback with an actual image composable.
 *
 * @param displayName  Full name used to generate initials (e.g. "John Doe" → "JD")
 * @param modifier     Modifier
 * @param size         Diameter of the avatar circle (default 40.dp)
 * @param backgroundColor Background color — defaults to [MaterialTheme.colorScheme.primary]
 * @param contentColor   Text color — defaults to [MaterialTheme.colorScheme.onPrimary]
 */
@Composable
fun AppAvatar(
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    backgroundColor: Color? = null,
    contentColor: Color? = null
) {
    val bg = backgroundColor ?: MaterialTheme.colorScheme.primary
    val fg = contentColor ?: MaterialTheme.colorScheme.onPrimary
    val initials = displayName.toInitials()
    val fontSize = (size.value * 0.35f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = fg,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

/** Extract up to 2 initials from a display name. "John Doe" → "JD", "Admin" → "A". */
private fun String.toInitials(): String {
    val parts = trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AppAvatarPreview() {
    AppTheme {
        AppAvatar(displayName = "John Doe")
    }
}

@Preview(showBackground = true)
@Composable
private fun AppAvatarLargePreview() {
    AppTheme {
        AppAvatar(displayName = "Admin User", size = 64.dp)
    }
}
