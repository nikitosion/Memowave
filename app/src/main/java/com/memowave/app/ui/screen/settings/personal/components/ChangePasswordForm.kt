package com.memowave.app.ui.screen.settings.personal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.AuthSecuredTextField
import com.memowave.app.ui.screen.authentification.components.PasswordRule
import com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState

@Composable
fun ChangePasswordForm(
    currentPassword: String,
    newPassword: String,
    repeatPassword: String,
    validation: PasswordValidationState,
    passwordsMatch: Boolean,
    isSubmitting: Boolean,
    canSubmit: Boolean,
    onCurrentChange: (String) -> Unit,
    onNewChange: (String) -> Unit,
    onRepeatChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AuthSecuredTextField(
            value = currentPassword,
            onValueChange = onCurrentChange,
            labelText = stringResource(R.string.settings_personal_current_password),
            placeholderText = stringResource(R.string.settings_personal_current_password),
            leadingIconResId = R.drawable.round_lock_24,
            imeAction = ImeAction.Next
        )
        AuthSecuredTextField(
            value = newPassword,
            onValueChange = onNewChange,
            labelText = stringResource(R.string.settings_personal_new_password),
            placeholderText = stringResource(R.string.settings_personal_new_password),
            leadingIconResId = R.drawable.round_lock_24,
            imeAction = ImeAction.Next
        )
        Column(
            modifier = Modifier.padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PasswordRule(
                rule = stringResource(R.string.signup_password_rule_min_length),
                isSatisfied = validation.hasMinLength
            )
            PasswordRule(
                rule = stringResource(R.string.signup_password_rule_lowercase),
                isSatisfied = validation.hasLowercase
            )
            PasswordRule(
                rule = stringResource(R.string.signup_password_rule_uppercase),
                isSatisfied = validation.hasUppercase
            )
            PasswordRule(
                rule = stringResource(R.string.signup_password_rule_digit),
                isSatisfied = validation.hasDigit
            )
            PasswordRule(
                rule = stringResource(R.string.signup_password_rule_special),
                isSatisfied = validation.hasSpecialChar
            )
        }
        AuthSecuredTextField(
            value = repeatPassword,
            onValueChange = onRepeatChange,
            labelText = stringResource(R.string.settings_personal_repeat_password),
            placeholderText = stringResource(R.string.settings_personal_repeat_password),
            leadingIconResId = R.drawable.round_lock_24,
            imeAction = ImeAction.Done
        )
        if (repeatPassword.isNotEmpty() && !passwordsMatch) {
            Text(
                text = stringResource(R.string.settings_personal_passwords_dont_match),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        AuthActionButton(
            onClick = onSubmit,
            isEnabled = canSubmit,
            isLoading = isSubmitting,
            text = stringResource(R.string.settings_personal_update_password)
        )
    }
}
