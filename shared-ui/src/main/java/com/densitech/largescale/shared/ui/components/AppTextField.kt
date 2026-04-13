package com.densitech.largescale.shared.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.densitech.largescale.shared.ui.theme.AppTheme
import com.densitech.largescale.shared.ui.theme.Spacing

/**
 * Standard outlined text field with label, optional error, and trailing icon.
 *
 * @param value         Current text value
 * @param onValueChange Called when the text changes
 * @param label         Field label shown as a floating hint
 * @param modifier      Modifier for the outer column
 * @param placeholder   Dimmed hint text shown when the field is empty
 * @param errorMessage  When non-null, shows below the field in error style
 * @param trailingIcon  Optional icon at the right end of the field
 * @param keyboardOptions Controls keyboard type and IME action
 * @param keyboardActions IME action callbacks (Next, Done, etc.)
 * @param singleLine    When true, the field does not wrap to multiple lines
 * @param enabled       When false, the field is read-only and dimmed
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorMessage: String? = null,
    trailingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            trailingIcon = trailingIcon?.let { icon ->
                { Icon(imageVector = icon, contentDescription = null) }
            },
            isError = errorMessage != null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Spacing.md, top = Spacing.xxs)
            )
        }
    }
}

/**
 * Password field with built-in show/hide toggle.
 *
 * @param value         Current password value
 * @param onValueChange Called when the text changes
 * @param label         Field label (default "Password")
 * @param modifier      Modifier for the outer column
 * @param errorMessage  When non-null, shows an error below the field
 * @param imeAction     IME action — typically [ImeAction.Done] for the last field
 * @param onImeAction   Called when the IME action is triggered
 */
@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(onAny = { onImeAction() }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = Spacing.md, top = Spacing.xxs)
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    AppTheme {
        AppTextField(
            value = "admin",
            onValueChange = {},
            label = "Username",
            modifier = Modifier.fillMaxWidth().padding(Spacing.md)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTextFieldErrorPreview() {
    AppTheme {
        AppTextField(
            value = "",
            onValueChange = {},
            label = "Username",
            errorMessage = "Username is required",
            modifier = Modifier.fillMaxWidth().padding(Spacing.md)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPasswordFieldPreview() {
    AppTheme {
        AppPasswordField(
            value = "secret",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().padding(Spacing.md)
        )
    }
}
