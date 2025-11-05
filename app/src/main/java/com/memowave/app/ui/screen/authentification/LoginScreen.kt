package com.memowave.app.ui.screen.authentification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.repository.AuthRepositoryImpl
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.AuthNotSecuredTextField
import com.memowave.app.ui.screen.authentification.components.AuthSecuredTextField
import com.memowave.app.ui.screen.authentification.components.DividersWithTextInMiddle
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.screen.authentification.components.OAuthButtons
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun LoginRoute(authViewModel: AuthViewModel, navController: NavController) {
    LoginScreen(viewModel = authViewModel, navController = navController)
}

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MemowaveLogoColored(size = 60.dp)

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Привет, это ",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                modifier = Modifier.offset(y = (-4).dp),
                text = "Memowave",
                style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FontFamily(Font(R.font.bagelfatone_regular)))
            )
            Text(
                text = "!",
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = "Лови волну новых слов и погружайся в язык с головой!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        OAuthButtons(modifier = Modifier.padding(top = 24.dp))

        AuthNotSecuredTextField(
            value = uiState.loginFormState.email,
            onValueChange = viewModel::onLoginEmailChanged,
            error = uiState.loginFormState.emailError,
            modifier = Modifier.padding(top = 20.dp),
            labelText = "Email",
            placeholderText = "Введите ваш email",
            leadingIconResId = R.drawable.round_alternate_email_24
        )

        AuthSecuredTextField(
            value = uiState.loginFormState.password,
            onValueChange = viewModel::onLoginPasswordChanged,
            error = uiState.loginFormState.passwordError,
            modifier = Modifier.padding(top = 16.dp),
            labelText = "Пароль",
            placeholderText = "Введите ваш пароль",
            leadingIconResId = R.drawable.round_lock_24
        )

        AuthActionButton(
            onClick = viewModel::onLoginClick,
            isEnabled = isButtonEnabled,
            isLoading = uiState.isLoading,
            modifier = Modifier.padding(top = 20.dp),
            text = "Войти"
        )

        OutlinedButton(
            modifier = Modifier.padding(top = 24.dp),
            onClick = {
                navController.navigate("forgot_password") {
                    popUpTo("login")
                }
            },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Text("Забыли пароль?")
        }

        DividersWithTextInMiddle(text = "ИЛИ", modifier = Modifier.padding(top = 16.dp))

        OutlinedButton(
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                navController.navigate("sign_up") {
                    popUpTo("login")
                }
            },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        ) {
            Text("Нет аккаунта? Зарегистрируйтесь")
        }
    }
}


@Preview
@Composable
fun LoginScreenPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            LoginScreen(AuthViewModel(loginUseCase = LoginUseCase(AuthRepositoryImpl(ApiService()))), navController = NavController(LocalContext.current))
        }
    }
}