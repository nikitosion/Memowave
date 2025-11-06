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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.repository.AuthRepositoryImpl
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.AuthNotSecuredTextField
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun ForgotPasswordRoute(authViewModel: AuthViewModel, navController: NavController) {
    ForgotPasswordScreen(viewModel = authViewModel, navController = navController)
}

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val isForgetButtonEnabled by viewModel.isForgotPasswordButtonEnabled.collectAsState()

    LaunchedEffect(uiState.isContinuedResetPassword) {
        if (uiState.isContinuedResetPassword) {
            navController.navigate("login") {
                popUpTo("forgot_password") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

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
                value = uiState.forgotPasswordForm.email,
                onValueChange = viewModel::onForgotPasswordEmailChanged,
                error = uiState.forgotPasswordForm.emailError,
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
                onClick = viewModel::onForgotPasswordClick,
                isEnabled = isForgetButtonEnabled,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(top = 20.dp),
                text = "Продолжить"
            )
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 64.dp),
            onClick = {
                navController.popBackStack()
            }
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

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            ForgotPasswordScreen(
                AuthViewModel(LoginUseCase(AuthRepositoryImpl(ApiService()))),
                navController = NavController(LocalContext.current)
            )
        }
    }
}