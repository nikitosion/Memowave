package com.memowave.app.ui.screen.authentification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.AuthNotSecuredTextField
import com.memowave.app.ui.screen.authentification.components.AuthSecuredTextField
import com.memowave.app.ui.screen.authentification.components.DividersWithTextInMiddle
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.screen.authentification.components.OAuthButtons
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Stateless UI for the Login screen.
 *
 * Displays login form, OAuth buttons, and navigation actions.
 * All state and actions are passed as parameters for easy preview and testing.
 *
 * @param email Email input value
 * @param emailError Error message for email field
 * @param password Password input value
 * @param passwordError Error message for password field
 * @param isLoading Whether to show loading indicator
 * @param isButtonEnabled Whether the login button is enabled
 * @param onEmailChange Callback for email input changes
 * @param onPasswordChange Callback for password input changes
 * @param onLoginClick Callback for login button click
 * @param onForgotPasswordClick Callback for forgot password button click
 * @param onSignUpClick Callback for sign up button click
 */
@Composable
fun LoginScreenContent(
    email: String,
    emailError: String?,
    password: String,
    passwordError: String?,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(top = 64.dp, bottom = 64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MemowaveLogoColored(size = 60.dp)
            Row(
                modifier = Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.login_greeting),
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp).offset(y = (-4).dp),
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily(
                            Font(R.font.bagelfatone_regular)
                        )
                    )
                )
                Text(
                    text = stringResource(R.string.login_greeting_end),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            OAuthButtons(modifier = Modifier.padding(top = 24.dp))
            AuthNotSecuredTextField(
                value = email,
                onValueChange = onEmailChange,
                error = emailError,
                modifier = Modifier.padding(top = 20.dp),
                labelText = stringResource(R.string.login_email_label),
                placeholderText = stringResource(R.string.login_email_placeholder),
                leadingIconResId = R.drawable.round_alternate_email_24
            )
            AuthSecuredTextField(
                value = password,
                onValueChange = onPasswordChange,
                error = passwordError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = stringResource(R.string.login_password_label),
                placeholderText = stringResource(R.string.login_password_placeholder),
                leadingIconResId = R.drawable.round_lock_24
            )
            AuthActionButton(
                onClick = onLoginClick,
                isEnabled = isButtonEnabled,
                isLoading = isLoading,
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.login_button)
            )
            OutlinedButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = onForgotPasswordClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Text(stringResource(R.string.login_forgot_password))
            }
            DividersWithTextInMiddle(text = stringResource(R.string.login_or), modifier = Modifier.padding(top = 16.dp))
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                onClick = onSignUpClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            ) {
                Text(stringResource(R.string.login_signup))
            }
        }
    }
}

/**
 * Entry point for the Login screen with ViewModel and navigation.
 *
 * Observes state from the ViewModel and passes it to the stateless content.
 * Handles navigation to the main page and other screens.
 *
 * @param viewModel AuthViewModel instance
 * @param navController NavController for navigation
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val isButtonEnabled by viewModel.isLoginButtonEnabled.collectAsState()

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            navController.navigate("main_page") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LoginScreenContent(
        email = uiState.loginFormState.email,
        emailError = uiState.loginFormState.emailError,
        password = uiState.loginFormState.password,
        passwordError = uiState.loginFormState.passwordError,
        isLoading = uiState.isLoading,
        isButtonEnabled = isButtonEnabled,
        onEmailChange = viewModel::onLoginEmailChanged,
        onPasswordChange = viewModel::onLoginPasswordChanged,
        onLoginClick = viewModel::onLoginClick,
        onForgotPasswordClick = { navController.navigate("forgot_password") },
        onSignUpClick = { navController.navigate("sign_up") }
    )
}

/**
 * Navigation wrapper for the Login screen.
 *
 * @param authViewModel AuthViewModel instance
 * @param navController NavController for navigation
 */
@Composable
fun LoginRoute(authViewModel: AuthViewModel, navController: NavController) {
    LoginScreen(viewModel = authViewModel, navController = navController)
}

/**
 * Preview for the Login screen content.
 *
 * Shows the stateless UI with sample data for design and testing purposes.
 */
@Preview(device = "spec:width=411dp,height=891dp")
@Composable
fun LoginScreenPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            LoginScreenContent(
                email = "",
                emailError = null,
                password = "",
                passwordError = null,
                isLoading = false,
                isButtonEnabled = true,
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onForgotPasswordClick = {},
                onSignUpClick = {}
            )
        }
    }
}