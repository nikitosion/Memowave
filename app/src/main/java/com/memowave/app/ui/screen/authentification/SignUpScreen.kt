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
import com.memowave.app.ui.screen.authentification.components.AuthSecuredTextField
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.screen.authentification.components.OAuthButtons
import com.memowave.app.ui.screen.authentification.components.PasswordRule
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun SignUpRoute(authViewModel: AuthViewModel, navController: NavController) {
    SignUpScreen(viewModel = authViewModel, navController = navController)
}

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
                popUpTo("sign_up") { inclusive = true }
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
                text = "Регистрация",
                style = MaterialTheme.typography.headlineLarge
            )

            OAuthButtons(modifier = Modifier.padding(top = 24.dp))
            /*DividersWithTextInMiddle(modifier = Modifier.padding(top = 20.dp))*/

            AuthNotSecuredTextField(
                value = uiState.signUpFormState.name,
                onValueChange = viewModel::onNameChanged,
                error = uiState.signUpFormState.nameError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = "Ваше имя",
                placeholderText = "Введите ваше имя",
                leadingIconResId = R.drawable.round_person_24,
            )

            AuthNotSecuredTextField(
                value = uiState.signUpFormState.email,
                onValueChange = viewModel::onSignUpEmailChanged,
                error = uiState.signUpFormState.emailError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = "Email",
                placeholderText = "Введите ваш email",
                leadingIconResId = R.drawable.round_alternate_email_24,
            )

            AuthSecuredTextField(
                value = uiState.signUpFormState.password,
                onValueChange = viewModel::onSignUpPasswordChanged,
                modifier = Modifier.padding(top = 16.dp),
                labelText = "Пароль",
                placeholderText = "Придумайте пароль",
                leadingIconResId = R.drawable.round_lock_24,
                imeAction = ImeAction.Next
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                rule = "Содержит не менее 8 символов",
                isSatisfied = uiState.signUpFormState.passwordValidationState.hasMinLength
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = "Содержит строчную букву",
                isSatisfied = uiState.signUpFormState.passwordValidationState.hasLowercase
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = "Содержит заглавную букву",
                isSatisfied = uiState.signUpFormState.passwordValidationState.hasUppercase
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = "Содержит цифру",
                isSatisfied = uiState.signUpFormState.passwordValidationState.hasDigit
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = "Содержит специальный символ",
                isSatisfied = uiState.signUpFormState.passwordValidationState.hasSpecialChar
            )

            AuthSecuredTextField(
                value = uiState.signUpFormState.repeatedPassword,
                onValueChange = viewModel::onRepeatedPasswordChanged,
                error = uiState.signUpFormState.repeatedPasswordError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = "Подтвердите пароль",
                placeholderText = "Повторите пароль",
                leadingIconResId = R.drawable.round_lock_24,
            )

            AuthActionButton(
                text = "Продолжить",
                onClick = viewModel::onSignUpClick,
                isEnabled = isSignUpButtonEnabled,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(top = 24.dp)
            )

            OutlinedButton(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    navController.navigate("login") {
                        popUpTo("sign_up") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Text("Уже есть аккаунт? Войти")
            }
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

@Preview(device = "spec:width=411dp,height=891dp,cutout=double", showSystemUi = true, showBackground = false)
@Composable
fun SingUpScreenPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            SignUpScreen(
                viewModel = AuthViewModel(LoginUseCase(AuthRepositoryImpl(ApiService()))),
                navController = NavController(LocalContext.current)
            )

        }
    }
}