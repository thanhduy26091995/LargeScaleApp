package com.densitech.largescale.feature.core.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.densitech.largescale.contracts.AuthService
import com.densitech.largescale.contracts.Routes
import com.densitech.largescale.shared.ui.components.LoadingIndicator
import com.densitech.largescale.shared.ui.theme.AppTheme
import com.densitech.largescale.shared.ui.theme.Spacing
import kotlinx.coroutines.delay

/**
 * Entry point screen. Attempts to restore a saved session.
 * Navigates to HOME on success, LOGIN on failure.
 *
 * @param authService   Injected at the call site (from `:app` or NavGraph setup)
 * @param onNavigate    Lambda accepting the target route — caller drives navigation
 */
@Composable
fun SplashScreen(
    authService: AuthService,
    onNavigate: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(500) // Brief pause for splash visibility
        val user = authService.restoreSession()
        onNavigate(if (user != null) Routes.HOME else Routes.LOGIN)
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LargeScale",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Modular · Multi-tenant",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Spacing.lg))
            LoadingIndicator(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    AppTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LargeScale", style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(Spacing.sm))
                Text("Modular · Multi-tenant", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
