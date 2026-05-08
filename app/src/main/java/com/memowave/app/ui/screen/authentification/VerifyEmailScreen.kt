package com.memowave.app.ui.screen.authentification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.screen.authentification.components.OtpTextField
import com.memowave.app.ui.screen.authentification.helper.AuthNavEvent
import com.memowave.app.ui.screen.authentification.helper.VerifyEmailFlow
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Stateless UI for the email-verification screen.
 *
 * @param code Current OTP value (digits only, length 0..[Companion].CODE_LENGTH)
 * @param codeError Inline error message under the OTP field, or null
 * @param isLoading Whether the verify request is in flight
 * @param isButtonEnabled Whether the submit button is enabled
 * @param resendCooldownSec Seconds remaining until resend is available (0 = enabled)
 * @param onCodeChange OTP value change callback
 * @param onSubmit Submit-click callback
 * @param onResendCode Resend-click callback
 */
@Composable
fun VerifyEmailScreenContent(
    code: String,
    codeError: String?,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    resendCooldownSec: Int,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onResendCode: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
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
                text = stringResource(R.string.auth_verify_email_title),
                style = MaterialTheme.typography.headlineLarge,
            )

            Text(
                modifier = Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp),
                text = stringResource(R.string.auth_verify_email_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            OtpTextField(
                value = code,
                onValueChange = onCodeChange,
                modifier = Modifier.padding(top = 24.dp),
                isError = codeError != null,
            )

            if (codeError != null) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = codeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }

            AuthActionButton(
                onClick = onSubmit,
                isEnabled = isButtonEnabled,
                isLoading = isLoading,
                text = stringResource(R.string.auth_verify_email_submit),
                modifier = Modifier.padding(top = 24.dp),
            )

            TextButton(
                onClick = onResendCode,
                enabled = resendCooldownSec == 0 && !isLoading,
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Text(
                    text = if (resendCooldownSec == 0) {
                        stringResource(R.string.auth_verify_email_resend)
                    } else {
                        stringResource(R.string.auth_verify_email_resend_in, resendCooldownSec)
                    },
                )
            }
        }
    }
}

/**
 * Entry point for the email-verification screen with ViewModel and navigation.
 *
 * On success, navigation depends on [VerifyEmailViewModel.flow]:
 *  - [VerifyEmailFlow.SignUp] → [Screen.MainPage], clearing the back stack
 *  - [VerifyEmailFlow.ForgotPassword] → [Screen.ResetPassword]
 */
@Composable
fun VerifyEmailScreen(
    viewModel: VerifyEmailViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val isButtonEnabled by viewModel.isButtonEnabled.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is AuthNavEvent.ToMain -> {
                    navController.navigate(Screen.MainPage.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is AuthNavEvent.ToResetPassword -> {
                    navController.navigate("reset_password/${event.userId}") {
                        popUpTo(Screen.Login.route)
                        launchSingleTop = true
                    }
                }
                else -> Unit
            }
        }
    }

    VerifyEmailScreenContent(
        code = uiState.form.code,
        codeError = uiState.form.codeError,
        isLoading = uiState.isLoading,
        isButtonEnabled = isButtonEnabled,
        resendCooldownSec = uiState.resendCooldownSec,
        onCodeChange = viewModel::onCodeChanged,
        onSubmit = viewModel::onSubmit,
        onResendCode = viewModel::onResendCode,
    )
}

/** Navigation wrapper for the email-verification screen. */
@Composable
fun VerifyEmailRoute(viewModel: VerifyEmailViewModel, navController: NavController) {
    VerifyEmailScreen(viewModel = viewModel, navController = navController)
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
private fun VerifyEmailScreenPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
        ) {
            VerifyEmailScreenContent(
                code = "12",
                codeError = null,
                isLoading = false,
                isButtonEnabled = false,
                resendCooldownSec = 0,
                onCodeChange = {},
                onSubmit = {},
                onResendCode = {},
            )
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
private fun VerifyEmailScreenErrorPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
        ) {
            VerifyEmailScreenContent(
                code = "98765",
                codeError = "Invalid or expired code",
                isLoading = false,
                isButtonEnabled = true,
                resendCooldownSec = 42,
                onCodeChange = {},
                onSubmit = {},
                onResendCode = {},
            )
        }
    }
}
