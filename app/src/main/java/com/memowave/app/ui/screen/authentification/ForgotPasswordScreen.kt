package com.memowave.app.ui.screen.authentification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.repository.AuthRepositoryImpl
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.EmailTextField
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.memowave_logo_colored_no_surface),
            contentDescription = "Memowave Logo",
            modifier = Modifier.height(60.dp)
        )
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = "Восстановление пароля",
            style = MaterialTheme.typography.headlineLarge,
        )

        EmailTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            error = uiState.emailError,
            modifier = Modifier.padding(top = 20.dp)
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
}

@Preview
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