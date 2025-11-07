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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.AuthNotSecuredTextField
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Stateless UI for the Forgot Password screen.
 *
 * Displays a form for entering an email to recover a password.
 * All state and actions are passed as parameters for easy preview and testing.
 *
 * @param email Current email input value
 * @param emailError Error message for the email field, or null
 * @param isLoading Whether the loading indicator should be shown
 * @param isButtonEnabled Whether the continue button is enabled
 * @param onEmailChange Callback for email input changes
 * @param onContinueClick Callback for continue button click
 * @param onBackClick Callback for back button click
 */
@Composable
fun ForgotPasswordScreenContent(
    email: String,
    emailError: String?,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    onEmailChange: (String) -> Unit,
    onContinueClick: () -> Unit,
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
                text = "Восстановление пароля",
                style = MaterialTheme.typography.headlineLarge,
            )

            AuthNotSecuredTextField(
                value = email,
                onValueChange = onEmailChange,
                error = emailError,
                modifier = Modifier.padding(top = 20.dp),
                labelText = "Email",
                placeholderText = "Введите ваш email",
                leadingIconResId = R.drawable.round_alternate_email_24,
                imeAction = ImeAction.Done
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
                text = "Мы отправим вам письмо с инструкцией по восстановлению",
                style = MaterialTheme.typography.bodySmall,
            )

            AuthActionButton(
                onClick = onContinueClick,
                isEnabled = isButtonEnabled,
                isLoading = isLoading,
                modifier = Modifier.padding(top = 20.dp),
                text = "Продолжить"
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
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * Entry point for the Forgot Password screen with ViewModel and navigation.
 *
 * Observes state from the ViewModel and passes it to the stateless content.
 * Handles navigation to the reset password screen.
 *
 * @param viewModel AuthViewModel instance
 * @param navController NavController for navigation
 */
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val isForgetButtonEnabled by viewModel.isForgotPasswordButtonEnabled.collectAsState()

    LaunchedEffect(uiState.isContinuedResetPassword) {
        if (uiState.isContinuedResetPassword) {
            navController.navigate("reset_password")
        }
    }

    ForgotPasswordScreenContent(
        email = uiState.forgotPasswordForm.email,
        emailError = uiState.forgotPasswordForm.emailError,
        isLoading = uiState.isLoading,
        isButtonEnabled = isForgetButtonEnabled,
        onEmailChange = viewModel::onForgotPasswordEmailChanged,
        onContinueClick = viewModel::onForgotPasswordClick,
        onBackClick = { navController.popBackStack() }
    )
}

/**
 * Navigation wrapper for the Forgot Password screen.
 *
 * @param authViewModel AuthViewModel instance
 * @param navController NavController for navigation
 */
@Composable
fun ForgotPasswordRoute(authViewModel: AuthViewModel, navController: NavController) {
    ForgotPasswordScreen(viewModel = authViewModel, navController = navController)
}

/**
 * Preview for the Forgot Password screen content.
 *
 * Shows the stateless UI with sample data for design and testing purposes.
 */
@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    // State для превью
    val email = ""
    val emailError: String? = null
    val isLoading = false
    val isButtonEnabled = true
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            ForgotPasswordScreenContent(
                email = email,
                emailError = emailError,
                isLoading = isLoading,
                isButtonEnabled = isButtonEnabled,
                onEmailChange = {},
                onContinueClick = {},
                onBackClick = {}
            )
        }
    }
}