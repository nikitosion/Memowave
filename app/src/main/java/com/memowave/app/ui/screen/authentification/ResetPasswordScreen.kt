package com.memowave.app.ui.screen.authentification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.AuthSecuredTextField
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.screen.authentification.components.PasswordRule
import com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Stateless UI for the Reset Password screen.
 *
 * Displays form for entering and confirming a new password, with password rules.
 * All state and actions are passed as parameters for easy preview and testing.
 *
 * @param newPassword New password input value
 * @param newPasswordError Error message for new password field
 * @param passwordValidationState State of password rules
 * @param repeatedNewPassword Repeated new password input value
 * @param repeatedNewPasswordError Error message for repeated password field
 * @param isLoading Whether to show loading indicator
 * @param isButtonEnabled Whether the reset button is enabled
 * @param onNewPasswordChange Callback for new password input changes
 * @param onRepeatedNewPasswordChange Callback for repeated password input changes
 * @param onResetClick Callback for reset button click
 * @param onBackClick Callback for back button click
 */
@Composable
fun ResetPasswordScreenContent(
    newPassword: String,
    newPasswordError: String?,
    passwordValidationState: PasswordValidationState,
    repeatedNewPassword: String,
    repeatedNewPasswordError: String?,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    onNewPasswordChange: (String) -> Unit,
    onRepeatedNewPasswordChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(top = 64.dp, bottom = 64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MemowaveLogoColored(size = 60.dp)
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.reset_password_title),
                style = MaterialTheme.typography.headlineLarge,
            )
            AuthSecuredTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                modifier = Modifier.padding(top = 16.dp),
                error = newPasswordError,
                labelText = stringResource(R.string.reset_password_new_label),
                placeholderText = stringResource(R.string.reset_password_new_placeholder),
                leadingIconResId = R.drawable.round_lock_24,
                imeAction = ImeAction.Next
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                rule = stringResource(R.string.reset_password_rule_min_length),
                isSatisfied = passwordValidationState.hasMinLength
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = stringResource(R.string.reset_password_rule_lowercase),
                isSatisfied = passwordValidationState.hasLowercase
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = stringResource(R.string.reset_password_rule_uppercase),
                isSatisfied = passwordValidationState.hasUppercase
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = stringResource(R.string.reset_password_rule_digit),
                isSatisfied = passwordValidationState.hasDigit
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = stringResource(R.string.reset_password_rule_special),
                isSatisfied = passwordValidationState.hasSpecialChar
            )
            AuthSecuredTextField(
                value = repeatedNewPassword,
                onValueChange = onRepeatedNewPasswordChange,
                error = repeatedNewPasswordError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = stringResource(R.string.reset_password_repeat_label),
                placeholderText = stringResource(R.string.reset_password_repeat_placeholder),
                leadingIconResId = R.drawable.round_lock_24,
            )
            AuthActionButton(
                onClick = onResetClick,
                isEnabled = isButtonEnabled,
                isLoading = isLoading,
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.reset_password_button)
            )
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 64.dp),
            onClick = onBackClick
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.round_chevron_left_24),
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * Entry point for the Reset Password screen with ViewModel and navigation.
 *
 * Observes state from the ViewModel and passes it to the stateless content.
 * Handles navigation to the login screen after success.
 *
 * @param viewModel AuthViewModel instance
 * @param navController NavController for navigation
 */
@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val uiState = viewModel.uiState.collectAsState().value
    val isResetPasswordButtonEnabled = viewModel.isResetPasswordButtonEnabled.collectAsState().value

    LaunchedEffect(uiState.isResetPasswordSuccess) {
        if (uiState.isResetPasswordSuccess) {
            navController.navigate("login") {
                popUpTo(Screen.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val passwordError = if (!uiState.forgotPasswordForm.newPassword.isEmpty() && !uiState.forgotPasswordForm.passwordValidationState.isAllValid) stringResource(R.string.password_not_valid) else null

    ResetPasswordScreenContent(
        newPassword = uiState.forgotPasswordForm.newPassword,
        newPasswordError = passwordError,
        passwordValidationState = uiState.forgotPasswordForm.passwordValidationState,
        repeatedNewPassword = uiState.forgotPasswordForm.repeatedNewPassword,
        repeatedNewPasswordError = uiState.forgotPasswordForm.repeatedNewPasswordError,
        isLoading = uiState.isLoading,
        isButtonEnabled = isResetPasswordButtonEnabled,
        onNewPasswordChange = viewModel::onResetPasswordNewPasswordChanged,
        onRepeatedNewPasswordChange = viewModel::onResetPasswordRepeatedNewPasswordChanged,
        onResetClick = viewModel::onResetPasswordClick,
        onBackClick = { navController.popBackStack() }
    )
}

/**
 * Navigation wrapper for the Reset Password screen.
 *
 * @param authViewModel AuthViewModel instance
 * @param navController NavController for navigation
 */
@Composable
fun ResetPasswordRoute(authViewModel: AuthViewModel, navController: NavController) {
    ResetPasswordScreen(viewModel = authViewModel, navController = navController)
}

/**
 * Preview for the Reset Password screen content.
 *
 * Shows the stateless UI with sample data for design and testing purposes.
 */
@Preview
@Composable
fun ResetPasswordScreenPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            ResetPasswordScreenContent(
                newPassword = "",
                newPasswordError = null,
                passwordValidationState = PasswordValidationState(),
                repeatedNewPassword = "",
                repeatedNewPasswordError = null,
                isLoading = false,
                isButtonEnabled = true,
                onNewPasswordChange = {},
                onRepeatedNewPasswordChange = {},
                onResetClick = {},
                onBackClick = {}
            )
        }
    }
}