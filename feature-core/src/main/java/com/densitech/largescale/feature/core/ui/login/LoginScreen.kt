package com.densitech.largescale.feature.core.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.densitech.largescale.shared.ui.components.AppButton
import com.densitech.largescale.shared.ui.components.AppPasswordField
import com.densitech.largescale.shared.ui.components.AppTextField
import com.densitech.largescale.shared.ui.theme.AppTheme
import com.densitech.largescale.shared.ui.theme.Spacing

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenHorizontal),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(Spacing.xl))

        AppTextField(
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            label = "Username",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(Spacing.md))

        AppPasswordField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            errorMessage = state.errorMessage,
            imeAction = ImeAction.Done,
            onImeAction = { viewModel.login(onLoginSuccess) }
        )

        Spacer(Modifier.height(Spacing.lg))

        AppButton(
            text = "Sign In",
            onClick = { viewModel.login(onLoginSuccess) },
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isLoading
        )

        // Hint for testers
        Spacer(Modifier.height(Spacing.xl))
        Text(
            text = "Test accounts: admin/admin · staff/staff · customer/customer",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    AppTheme {
        // Preview with static state — ViewModel not available in preview
        Column(
            modifier = Modifier.fillMaxSize().padding(Spacing.screenHorizontal),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
            Text("Sign in to continue", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(Spacing.xl))
            AppTextField(value = "admin", onValueChange = {}, label = "Username",
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(Spacing.md))
            AppPasswordField(value = "••••••", onValueChange = {},
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(Spacing.lg))
            AppButton(text = "Sign In", onClick = {}, modifier = Modifier.fillMaxWidth())
        }
    }
}
