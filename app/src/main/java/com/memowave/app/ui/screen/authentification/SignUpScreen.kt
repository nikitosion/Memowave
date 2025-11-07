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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.AuthNotSecuredTextField
import com.memowave.app.ui.screen.authentification.components.AuthSecuredTextField
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.screen.authentification.components.OAuthButtons
import com.memowave.app.ui.screen.authentification.components.PasswordRule
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Stateless UI for the Sign Up screen.
 *
 * Displays registration form, password rules, and navigation actions.
 * All state and actions are passed as parameters for easy preview and testing.
 *
 * @param username Username input value
 * @param usernameError Error message for username field
 * @param email Email input value
 * @param emailError Error message for email field
 * @param password Password input value
 * @param passwordError Error message for password field
 * @param passwordValidationState State of password rules
 * @param repeatedPassword Repeated password input value
 * @param repeatedPasswordError Error message for repeated password field
 * @param isLoading Whether to show loading indicator
 * @param isButtonEnabled Whether the sign up button is enabled
 * @param onUsernameChange Callback for username input changes
 * @param onEmailChange Callback for email input changes
 * @param onPasswordChange Callback for password input changes
 * @param onRepeatedPasswordChange Callback for repeated password input changes
 * @param onSignUpClick Callback for sign up button click
 * @param onLoginClick Callback for login button click
 * @param onBackClick Callback for back button click
 */
@Composable
fun SignUpScreenContent(
    username: String,
    usernameError: String?,
    email: String,
    emailError: String?,
    password: String,
    passwordError: String?,
    passwordValidationState: com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState,
    repeatedPassword: String,
    repeatedPasswordError: String?,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatedPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 64.dp, bottom = 64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MemowaveLogoColored(size = 60.dp)
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.signup_title),
                style = MaterialTheme.typography.headlineLarge
            )
            OAuthButtons(modifier = Modifier.padding(top = 24.dp))
            AuthNotSecuredTextField(
                value = username,
                onValueChange = onUsernameChange,
                error = usernameError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = stringResource(R.string.signup_name_label),
                placeholderText = stringResource(R.string.signup_name_placeholder),
                leadingIconResId = R.drawable.round_person_24,
            )
            AuthNotSecuredTextField(
                value = email,
                onValueChange = onEmailChange,
                error = emailError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = stringResource(R.string.signup_email_label),
                placeholderText = stringResource(R.string.signup_email_placeholder),
                leadingIconResId = R.drawable.round_alternate_email_24,
            )
            AuthSecuredTextField(
                value = password,
                onValueChange = onPasswordChange,
                error = passwordError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = stringResource(R.string.signup_password_label),
                placeholderText = stringResource(R.string.signup_password_placeholder),
                leadingIconResId = R.drawable.round_lock_24,
                imeAction = ImeAction.Next
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                rule = stringResource(R.string.signup_password_rule_min_length),
                isSatisfied = passwordValidationState.hasMinLength
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = stringResource(R.string.signup_password_rule_lowercase),
                isSatisfied = passwordValidationState.hasLowercase
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = stringResource(R.string.signup_password_rule_uppercase),
                isSatisfied = passwordValidationState.hasUppercase
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = stringResource(R.string.signup_password_rule_digit),
                isSatisfied = passwordValidationState.hasDigit
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = stringResource(R.string.signup_password_rule_special),
                isSatisfied = passwordValidationState.hasSpecialChar
            )
            AuthSecuredTextField(
                value = repeatedPassword,
                onValueChange = onRepeatedPasswordChange,
                error = repeatedPasswordError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = stringResource(R.string.signup_repeat_password_label),
                placeholderText = stringResource(R.string.signup_repeat_password_placeholder),
                leadingIconResId = R.drawable.round_lock_24,
            )
            AuthActionButton(
                text = stringResource(R.string.signup_continue),
                onClick = onSignUpClick,
                isEnabled = isButtonEnabled,
                isLoading = isLoading,
                modifier = Modifier.padding(top = 24.dp)
            )
            OutlinedButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = onLoginClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Text(stringResource(R.string.signup_login))
            }
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
 * Entry point for the Sign Up screen with ViewModel and navigation.
 *
 * Observes state from the ViewModel and passes it to the stateless content.
 * Handles navigation to the login screen and back.
 *
 * @param viewModel AuthViewModel instance
 * @param navController NavController for navigation
 */
@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val uiState = viewModel.uiState.collectAsState().value
    val isSignUpButtonEnabled = viewModel.isSignUpButtonEnabled.collectAsState().value

    LaunchedEffect(uiState.isContinuedSignUp) {
        if (uiState.isContinuedSignUp) {
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    SignUpScreenContent(
        username = uiState.signUpFormState.username,
        usernameError = uiState.signUpFormState.nameError,
        email = uiState.signUpFormState.email,
        emailError = uiState.signUpFormState.emailError,
        password = uiState.signUpFormState.password,
                passwordError = if (uiState.signUpFormState.password.isNotEmpty() && !uiState.signUpFormState.passwordValidationState.isAllValid) "Пароль не соответствует требованиям" else null,
        passwordValidationState = uiState.signUpFormState.passwordValidationState,
        repeatedPassword = uiState.signUpFormState.repeatedPassword,
        repeatedPasswordError = uiState.signUpFormState.repeatedPasswordError,
        isLoading = uiState.isLoading,
        isButtonEnabled = isSignUpButtonEnabled,
        onUsernameChange = viewModel::onNameChanged,
        onEmailChange = viewModel::onSignUpEmailChanged,
        onPasswordChange = viewModel::onSignUpPasswordChanged,
        onRepeatedPasswordChange = viewModel::onRepeatedPasswordChanged,
        onSignUpClick = viewModel::onSignUpClick,
        onLoginClick = {
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        },
        onBackClick = { navController.popBackStack() }
    )
}

/**
 * Navigation wrapper for the Sign Up screen.
 *
 * @param authViewModel AuthViewModel instance
 * @param navController NavController for navigation
 */
@Composable
fun SignUpRoute(authViewModel: AuthViewModel, navController: NavController) {
    SignUpScreen(viewModel = authViewModel, navController = navController)
}

/**
 * Preview for the Sign Up screen content.
 *
 * Shows the stateless UI with sample data for design and testing purposes.
 */
@Preview(
    device = "spec:width=411dp,height=891dp,cutout=double",
    showSystemUi = true,
    showBackground = false
)
@Composable
fun SingUpScreenPreview() {
    val passwordValidationState = com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState()
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            SignUpScreenContent(
                username = "",
                usernameError = null,
                email = "",
                emailError = null,
                password = "",
                passwordError = null,
                passwordValidationState = passwordValidationState,
                repeatedPassword = "",
                repeatedPasswordError = null,
                isLoading = false,
                isButtonEnabled = true,
                onUsernameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onRepeatedPasswordChange = {},
                onSignUpClick = {},
                onLoginClick = {},
                onBackClick = {}
            )

        }
    }
}